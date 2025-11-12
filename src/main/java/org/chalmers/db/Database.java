package org.chalmers.db;

import org.chalmers.model.User;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class Database {

    private static final String DATABASE_URL = "jdbc:sqlite:app.db";

    private static ConnectionSource connectionSource;
    private static Dao<User, Integer> userDao;

    public static void init() throws SQLException {
        if (connectionSource != null) {
            return;
        }

        try {
            // Se till att drivrutinen laddas
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Kunde inte ladda SQLite-drivrutin", e);
        }

        connectionSource = new JdbcConnectionSource(DATABASE_URL);

        // Skapa tabell om den inte finns
        TableUtils.createTableIfNotExists(connectionSource, User.class);

        // Skapa DAO för User
        userDao = DaoManager.createDao(connectionSource, User.class);
    }

    public static Dao<User, Integer> getUserDao() throws SQLException {
        if (userDao == null) {
            init();
        }
        return userDao;
    }

    public static void close() {
        if (connectionSource != null) {
            try {
                connectionSource.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                connectionSource = null;
                userDao = null;
            }
        }
    }
}
