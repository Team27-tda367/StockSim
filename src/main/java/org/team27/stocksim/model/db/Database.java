package org.team27.stocksim.model.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class Database {

    private static final String DATABASE_URL = "jdbc:sqlite:app.db";

    private static ConnectionSource connectionSource;
    private static Dao<Placeholder, Integer> placeholderDao;

    public static void init() throws SQLException {
        if (connectionSource != null) {
            return;
        }

        try {
            // Ladda SQLite-drivrutinen
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Kunde inte ladda SQLite-drivrutin", e);
        }

        connectionSource = new JdbcConnectionSource(DATABASE_URL);

        // Skapa placeholder-tabell om den inte finns
        TableUtils.createTableIfNotExists(connectionSource, Placeholder.class);

        // Skapa DAO
        placeholderDao = DaoManager.createDao(connectionSource, Placeholder.class);
    }

    public static Dao<Placeholder, Integer> getPlaceholderDao() throws SQLException {
        if (placeholderDao == null) {
            init();
        }
        return placeholderDao;
    }

    public static void close() {
        if (connectionSource != null) {
            try {
                connectionSource.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                connectionSource = null;
                placeholderDao = null;
            }
        }
    }

    /**
     * Minimal placeholder-entitet som ORMLite kräver.
     * Finns bara för att hålla ORM-koden levande tills riktiga modeller skapas.
     */
    @DatabaseTable(tableName = "placeholder")
    public static class Placeholder {

        @DatabaseField(generatedId = true)
        private int id;

        public Placeholder() {
            // ORMLite kräver en tom konstruktor
        }
    }
}
