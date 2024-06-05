package me.neovitalism.votereward.voteparty;

import me.neovitalism.neoapi.modloading.config.Configuration;
import me.neovitalism.neoapi.player.PlayerManager;
import me.neovitalism.neoapi.utils.ChatUtil;
import me.neovitalism.neoapi.utils.ColorUtil;
import me.neovitalism.votereward.VoteReward;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoteParty {
    private static boolean enabled = false;
    private static int targetVotes = 25;
    private static boolean commandEnabled = true;
    private static String commandResponse = null;
    private static List<String> globalCommands = new ArrayList<>();
    private static List<String> perPlayerCommands = new ArrayList<>();
    private static String onVoteMessage = null;
    private static String targetReachedMessage = null;

    private static int currentVotes = 0;

    public static void reload(Configuration votePartySection) {
        if(votePartySection == null) {
            enabled = false;
            return;
        }
        enabled = votePartySection.getBoolean("Enabled", false);
        targetVotes = votePartySection.getInt("TargetVotes", 25);
        Configuration votePartyCommandConfig = votePartySection.getSection("VotePartyCommand");
        if(votePartyCommandConfig != null) {
            commandEnabled = votePartyCommandConfig.getBoolean("Enabled", true);
            commandResponse = votePartyCommandConfig.getString("Message", null);
        } else commandEnabled = false;
        globalCommands = votePartySection.getStringList("GlobalCommands");
        perPlayerCommands = votePartySection.getStringList("PerPlayerCommands");
        onVoteMessage = votePartySection.getString("OnVoteMessage", null);
        targetReachedMessage = votePartySection.getString("TargetReachedMessage", null);
    }

    public static void increment(VoteReward instance, String username) {
        currentVotes++;
        Map<String, String> replacements = new HashMap<>();
        replacements.put("{player}", username);
        replacements.put("{count}", String.valueOf(currentVotes));
        replacements.put("{target}", String.valueOf(targetVotes));
        String votedMessage = ChatUtil.replaceReplacements(onVoteMessage, replacements);
        instance.adventure().all().sendMessage(ColorUtil.parseColour(votedMessage));
        if(currentVotes == targetVotes) {
            currentVotes = 0;
            String targetReached = ChatUtil.replaceReplacements(targetReachedMessage, replacements);
            for(String command : globalCommands) {
                instance.getServer().getCommandManager().executeWithPrefix(instance.getServer().getCommandSource(), command);
            }
            for(ServerPlayerEntity player : PlayerManager.getAllPlayers(instance)) {
                replacements.put("{player}", player.getName().getString());
                for(String command : perPlayerCommands) {
                    instance.getServer().getCommandManager().executeWithPrefix(instance.getServer().getCommandSource(),
                            ChatUtil.replaceReplacements(command, replacements));
                }
            }
            instance.adventure().all().sendMessage(ColorUtil.parseColour(targetReached));
        }
        saveCurrentVotes(instance);
    }

    private static File voteFile;

    public static void loadCurrentVotes(File currentVotesFile, Configuration currentVotesConfig) {
        voteFile = currentVotesFile;
        currentVotes = currentVotesConfig.getInt("CurrentVotes");
    }

    public static void saveCurrentVotes(VoteReward instance) {
        Configuration currentVotesConfig = new Configuration();
        currentVotesConfig.set("CurrentVotes", currentVotes);
        instance.saveConfig(voteFile, currentVotesConfig);
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static int getTargetVotes() {
        return targetVotes;
    }

    public static boolean isCommandEnabled() {
        return enabled && commandEnabled;
    }

    public static String getCommandResponse() {
        return commandResponse;
    }

    public static int getCurrentVotes() {
        return currentVotes;
    }
}
