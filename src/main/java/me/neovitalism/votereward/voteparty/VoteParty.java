package me.neovitalism.votereward.voteparty;

import me.neovitalism.neoapi.NeoAPI;
import me.neovitalism.neoapi.config.Configuration;
import me.neovitalism.neoapi.player.PlayerManager;
import me.neovitalism.neoapi.utils.ColorUtil;
import me.neovitalism.neoapi.utils.CommandUtil;
import me.neovitalism.neoapi.utils.StringUtil;
import me.neovitalism.votereward.VoteReward;
import me.neovitalism.votereward.config.VoteRewardConfig;
import me.neovitalism.votereward.data.MySQLDatabaseHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class VoteParty {
    private static int currentVotes = 0;


    public static void ensureTableExists() {
        if (VoteRewardConfig.useMySQL()) {
            try (Connection connection = MySQLDatabaseHandler.getConnection()) {
                String createTableQuery = "CREATE TABLE IF NOT EXISTS vote_party (" +
                        "id INT PRIMARY KEY, " +
                        "current_votes INT NOT NULL)";
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate(createTableQuery);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void increment(String username, String service) {
        VoteParty.currentVotes++;
        Map<String, String> replacements = new HashMap<>();
        replacements.put("{player}", username);
        replacements.put("{count}", String.valueOf(VoteParty.currentVotes));
        replacements.put("{target}", String.valueOf(VoteRewardConfig.getVotePartyTarget()));
        replacements.put("{service}", service);
        String votedMessage = StringUtil.replaceReplacements(VoteRewardConfig.getOnVoteMessage(), replacements);
        if (votedMessage != null && VoteParty.currentVotes % VoteRewardConfig.getMessageInterval() == 0) {
            NeoAPI.adventure().all().sendMessage(ColorUtil.parseColour(votedMessage));
        }
        VoteParty.testForParty(replacements);
        VoteReward.EXECUTOR.runTaskAsync(VoteParty::saveCurrentVotes);
    }

    public static void testForParty(Map<String, String> replacements) {
        if (VoteParty.currentVotes == VoteRewardConfig.getVotePartyTarget()) {
            VoteParty.currentVotes = 0;
            VoteParty.executeRewards(replacements);
        }
    }

    public static void executeRewards(String executor) {
        Map<String, String> replacements = new HashMap<>();
        replacements.put("{player}", executor);
        replacements.put("{count}", String.valueOf(VoteParty.currentVotes));
        replacements.put("{target}", String.valueOf(VoteRewardConfig.getVotePartyTarget()));
        replacements.put("{service}", "???");
        VoteParty.executeRewards(replacements);
    }

    public static void executeRewards(Map<String, String> replacements) {
        String message = StringUtil.replaceReplacements(VoteRewardConfig.getVotePartyCompletedMessage(), replacements);
        for (String command : VoteRewardConfig.getVotePartyGlobalCommands()) CommandUtil.executeServerCommand(command);
        for (ServerPlayerEntity player : PlayerManager.getOnlinePlayers()) {
            for (String playerCommand : VoteRewardConfig.getVotePartyPerPlayerCommands()) {
                String parsed = StringUtil.replaceReplacements(playerCommand, Map.of("{player}", player.getName().getString()));
                CommandUtil.executeServerCommand(parsed);
            }
        }
        NeoAPI.adventure().all().sendMessage(ColorUtil.parseColour(message));
    }

    public static int getCurrentVotes() {
        return VoteParty.currentVotes;
    }

    public static void setCurrentVotes(String executor, int newCount) {
        VoteParty.currentVotes = newCount;
        Map<String, String> replacements = new HashMap<>();
        replacements.put("{player}", executor);
        replacements.put("{count}", String.valueOf(VoteParty.currentVotes));
        replacements.put("{target}", String.valueOf(VoteRewardConfig.getVotePartyTarget()));
        replacements.put("{service}", "???");
        VoteParty.testForParty(replacements);
        VoteReward.EXECUTOR.runTaskAsync(VoteParty::saveCurrentVotes);
    }

    public static void loadCurrentVotes(Configuration currentVotesConfig) {
        if (VoteRewardConfig.useMySQL()) {
            try (Connection connection = MySQLDatabaseHandler.getConnection()) {
                String query = "SELECT current_votes FROM vote_party WHERE id = 1";
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    VoteParty.currentVotes = resultSet.getInt("current_votes");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                VoteParty.currentVotes = 0;
            }
        } else {
            if (currentVotesConfig == null) VoteParty.currentVotes = 0;
            else VoteParty.currentVotes = currentVotesConfig.getInt("CurrentVotes");
        }
    }


    public static void saveCurrentVotes() {
        if (VoteRewardConfig.useMySQL()) {
            try (Connection connection = MySQLDatabaseHandler.getConnection()) {
                String query = "INSERT INTO vote_party (id, current_votes) VALUES (1, ?) ON DUPLICATE KEY UPDATE current_votes = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setInt(1, VoteParty.currentVotes);
                statement.setInt(2, VoteParty.currentVotes);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            Configuration currentVotesConfig = new Configuration();
            currentVotesConfig.set("CurrentVotes", VoteParty.currentVotes);
            VoteReward.inst().saveConfig("current-votes.yml", currentVotesConfig);
        }
    }
}
