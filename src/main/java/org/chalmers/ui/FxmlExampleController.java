package org.chalmers.ui;

import org.chalmers.db.Database;
import org.chalmers.model.User;
import com.j256.ormlite.dao.Dao;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.util.List;

public class FxmlExampleController {

    @FXML
    private TextField nameField;

    @FXML
    private ListView<User> userList;

    @FXML
    public void initialize() {
        // Körs när FXML är laddat
        loadUsers();
    }

    @FXML
    private void onSaveUser() {
        String name = nameField.getText();
        if (name == null || name.isBlank()) {
            return;
        }

        try {
            Dao<User, Integer> userDao = Database.getUserDao();
            userDao.create(new User(name));

            nameField.clear();
            loadUsers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadUsers() {
        try {
            Dao<User, Integer> userDao = Database.getUserDao();
            List<User> users = userDao.queryForAll();
            userList.getItems().setAll(users);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
