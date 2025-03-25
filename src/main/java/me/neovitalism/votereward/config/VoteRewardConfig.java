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
    private static String databaseUrl;
    private static String databaseUser;
    private static String databasePassword;
    private static boolean useMySQL;

    public static void reload(Configuration config) {
        // Load voting settings
        voteCommandFeedback = config.getStringList("VoteCommandFeedback");
        commandsOnVote = config.getStringList("CommandsOnVote");
        storeOfflineVotes = config.getBoolean("StoreOfflineVotes");

        // Load vote party settings
        Configuration votePartySection = config.getSection("VoteParty");
        votePartiesEnabled = votePartySection == null || votePartySection.getBoolean("Enabled");
        assert votePartySection != null;
        votePartyTarget = votePartySection.getInt("TargetVotes", 25);
        useMySQL = votePartySection.getBoolean("UseMySQL", false); // Add the useMySQL setting
        votePartyCommandFeedback = votePartySection.getString("VotePartyCommandFeedback");
        votePartyGlobalCommands = votePartySection.getStringList("GlobalCommands");
        votePartyPerPlayerCommands = votePartySection.getStringList("PerPlayerCommands");
        onVoteMessage = votePartySection.getString("OnVoteMessage", null);
        messageInterval = votePartySection.getInt("MessageInterval", 1);
        votePartyCompletedMessage = votePartySection.getString("TargetReachedMessage", null);

        // Load database settings
        Configuration databaseSection = config.getSection("Database");
        if (databaseSection != null) {
            databaseUrl = databaseSection.getString("Url", "jdbc:mysql://localhost:3306/your_database");
            databaseUser = databaseSection.getString("User", "your_username");
            databasePassword = databaseSection.getString("Password", "your_password");
        }
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

    public static String getDatabaseUrl() {
        return databaseUrl;
    }

    public static String getDatabaseUser() {
        return databaseUser;
    }

    public static String getDatabasePassword() {
        return databasePassword;
    }

    public static boolean useMySQL() {
        return useMySQL;
    }
}
