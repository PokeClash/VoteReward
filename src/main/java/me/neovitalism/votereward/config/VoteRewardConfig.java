package me.neovitalism.votereward.config;

import me.neovitalism.neoapi.config.Configuration;

import java.util.List;

public class VoteRewardConfig {
    private static List<String> voteCommandFeedback;
    private static List<String> commandsOnVote;
    private static boolean storeOfflineVotes;

    private static boolean votePartiesEnabled;
    private static int votePartyTarget;
    private static String votePartyCommandFeedback;
    private static List<String> votePartyGlobalCommands;
    private static List<String> votePartyPerPlayerCommands;
    private static String onVoteMessage;
    private static int messageInterval;
    private static String votePartyCompletedMessage;

    public static void reload(Configuration config) {
        VoteRewardConfig.voteCommandFeedback = config.getStringList("VoteCommandFeedback");
        VoteRewardConfig.commandsOnVote = config.getStringList("CommandsOnVote");
        VoteRewardConfig.storeOfflineVotes = config.getBoolean("StoreOfflineVotes");

        Configuration votePartySection = config.getSection("VoteParty");
        VoteRewardConfig.votePartiesEnabled = votePartySection == null || votePartySection.getBoolean("Enabled");
        assert votePartySection != null;
        VoteRewardConfig.votePartyTarget = votePartySection.getInt("TargetVotes", 25);
        VoteRewardConfig.votePartyCommandFeedback = votePartySection.getString("VotePartyCommandFeedback");
        VoteRewardConfig.votePartyGlobalCommands = votePartySection.getStringList("GlobalCommands");
        VoteRewardConfig.votePartyPerPlayerCommands = votePartySection.getStringList("PerPlayerCommands");
        VoteRewardConfig.onVoteMessage = votePartySection.getString("OnVoteMessage", null);
        VoteRewardConfig.messageInterval = votePartySection.getInt("MessageInterval", 1);
        VoteRewardConfig.votePartyCompletedMessage = votePartySection.getString("TargetReachedMessage", null);
    }

    public static List<String> getVoteCommandFeedback() {
        return VoteRewardConfig.voteCommandFeedback;
    }

    public static List<String> getCommandsOnVote() {
        return VoteRewardConfig.commandsOnVote;
    }

    public static boolean shouldStoreOfflineVotes() {
        return VoteRewardConfig.storeOfflineVotes;
    }

    public static boolean isVotePartiesEnabled() {
        return VoteRewardConfig.votePartiesEnabled;
    }

    public static int getVotePartyTarget() {
        return VoteRewardConfig.votePartyTarget;
    }

    public static String getVotePartyCommandFeedback() {
        return VoteRewardConfig.votePartyCommandFeedback;
    }

    public static List<String> getVotePartyGlobalCommands() {
        return VoteRewardConfig.votePartyGlobalCommands;
    }

    public static List<String> getVotePartyPerPlayerCommands() {
        return VoteRewardConfig.votePartyPerPlayerCommands;
    }

    public static String getOnVoteMessage() {
        return VoteRewardConfig.onVoteMessage;
    }

    public static int getMessageInterval() {
        return VoteRewardConfig.messageInterval;
    }

    public static String getVotePartyCompletedMessage() {
        return VoteRewardConfig.votePartyCompletedMessage;
    }
}
