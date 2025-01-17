package me.neovitalism.votereward.voteparty;

import me.neovitalism.neoapi.NeoAPI;
import me.neovitalism.neoapi.config.Configuration;
import me.neovitalism.neoapi.player.PlayerManager;
import me.neovitalism.neoapi.utils.ColorUtil;
import me.neovitalism.neoapi.utils.CommandUtil;
import me.neovitalism.neoapi.utils.StringUtil;
import me.neovitalism.votereward.VoteReward;
import me.neovitalism.votereward.config.VoteRewardConfig;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;

public class VoteParty {
    private static int currentVotes = 0;

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
        if (VoteParty.currentVotes == VoteRewardConfig.getVotePartyTarget()) {
            VoteParty.currentVotes = 0;
            VoteParty.executeRewards(replacements);
        }
        VoteReward.EXECUTOR.runTaskAsync(VoteParty::saveCurrentVotes);
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

    public static void setCurrentVotes(int newCount) {
        VoteParty.currentVotes = newCount;
        VoteReward.EXECUTOR.runTaskAsync(VoteParty::saveCurrentVotes);
    }

    public static void loadCurrentVotes(Configuration currentVotesConfig) {
        if (currentVotesConfig == null) VoteParty.currentVotes = 0;
        else VoteParty.currentVotes = currentVotesConfig.getInt("CurrentVotes");
    }

    public static void saveCurrentVotes() {
        Configuration currentVotesConfig = new Configuration();
        currentVotesConfig.set("CurrentVotes", VoteParty.currentVotes);
        VoteReward.inst().saveConfig("current-votes.yml", currentVotesConfig);
    }
}
