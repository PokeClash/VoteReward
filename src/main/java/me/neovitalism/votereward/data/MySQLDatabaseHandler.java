package me.neovitalism.votereward.data;

import me.neovitalism.votereward.config.VoteRewardConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLDatabaseHandler {
    private static final String URL = VoteRewardConfig.getDatabaseUrl();
    private static final String USER = VoteRewardConfig.getDatabaseUser();
    private static final String PASSWORD = VoteRewardConfig.getDatabasePassword();

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

