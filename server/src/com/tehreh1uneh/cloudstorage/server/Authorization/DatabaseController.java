package com.tehreh1uneh.cloudstorage.server.Authorization;

import java.sql.*;

public final class DatabaseController implements AuthorizeManager {

    private Connection connection;
    private PreparedStatement authQuery;

    @Override
    public void initialize() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:server/CloudStorage_DB.sqlite3");
            authQuery = connection.prepareStatement("SELECT id FROM users WHERE login=? AND password=?");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Не удалось инициализировать подключение к СУБД");
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
            e.printStackTrace();
            throw new RuntimeException("Ошибка при выполнении запроса авторизации");
        }
    }

    @Override
    public void dispose() {
        try {
            authQuery.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка при выгрузке ресурсов менеджера СУБД");
        }
    }
}
