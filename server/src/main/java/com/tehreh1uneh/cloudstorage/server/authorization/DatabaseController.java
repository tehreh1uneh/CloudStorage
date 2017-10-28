package com.tehreh1uneh.cloudstorage.server.authorization;

import org.apache.log4j.Logger;

import java.sql.*;

public final class DatabaseController implements AuthorizeManager {

    private static final Logger logger = Logger.getLogger(DatabaseController.class);

    private Connection connection;
    private PreparedStatement authQuery;
    private PreparedStatement checkUserNameQuery;
    private PreparedStatement regQuery;

    @Override
    public void initialize() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:server/src/main/resources/CloudStorage_DB.sqlite3");
            authQuery = connection.prepareStatement("SELECT id FROM users WHERE login=? AND password=?");

            checkUserNameQuery = connection.prepareStatement("SELECT id FROM users WHERE login=?");
            regQuery = connection.prepareStatement("INSERT INTO users (login,password) VALUES (?, ?)");
            logger.info("JDBC инициализирован");
        } catch (ClassNotFoundException | SQLException e) {
            logger.fatal("Ошибка инициализации JDBC", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean register(String login, String password) {
        try {
            checkUserNameQuery.setString(1, login);
            ResultSet res = checkUserNameQuery.executeQuery();
            if (res.next()) {
                return false;
            }

            regQuery.setString(1, login);
            regQuery.setString(2, password);
            regQuery.execute();
            return true;
        } catch (SQLException e) {
            logger.fatal("Ошибка запроса регистрации", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized boolean authorize(String login, String password) {
        try {
            authQuery.setString(1, login);
            authQuery.setString(2, password);
            ResultSet res = authQuery.executeQuery();
            return res.next();
        } catch (SQLException e) {
            logger.fatal("Ошибка запроса авторизации", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void dispose() {
        try {
            connection.close();
        } catch (SQLException e) {
            logger.fatal("Ошибка при выгрузке ресурсов менеджера СУБД", e);
            throw new RuntimeException(e);
        }
    }
}
