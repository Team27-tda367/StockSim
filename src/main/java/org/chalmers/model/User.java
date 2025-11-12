package org.chalmers.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "users")
public class User {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String name;

    public User() {
        // Krävs av ORMLite
    }

    public User(String name) {
        this.name = name;
    }

    // Getters/setters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        // Visas i ListView
        return name + " (id=" + id + ")";
    }
}
