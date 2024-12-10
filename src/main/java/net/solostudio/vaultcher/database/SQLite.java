package net.solostudio.vaultcher.database;

import lombok.Getter;
import net.solostudio.vaultcher.Vaultcher;
import net.solostudio.vaultcher.enums.keys.ConfigKeys;
import net.solostudio.vaultcher.managers.VaultcherData;
import net.solostudio.vaultcher.utils.LoggerUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class SQLite extends AbstractDatabase {
    private final Connection connection;

    public SQLite() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        File dataFolder = new File(Vaultcher.getInstance().getDataFolder(), "vaultcher.db");
        String url = "jdbc:sqlite:" + dataFolder;
        connection = DriverManager.getConnection(url);
    }

    @Override
    public boolean isConnected() {
        return connection != null;
    }

    @Override
    public void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException exception) {
                LoggerUtils.error("Error closing database connection: " + exception.getMessage());
            }
        }
    }

    public void createTable() {
        String query = "CREATE TABLE IF NOT EXISTS vaultcher (VAULTCHER VARCHAR(255) NOT NULL, COMMAND VARCHAR(255) NOT NULL, USES INT, OWNERS VARCHAR(255), PRIMARY KEY (VAULTCHER))";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
            preparedStatement.execute();
        } catch (SQLException exception) {
            LoggerUtils.error("Error creating table: " + exception.getMessage());
        }
    }

    @Override
    public void createVaultcher(@NotNull String name, @NotNull String cmd, int uses) {
        String query = "INSERT OR IGNORE INTO vaultcher (VAULTCHER, COMMAND, USES) VALUES (?, ?, ?)";

        try {
            if (!exists(name)) {
                try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                    preparedStatement.setString(1, name);
                    preparedStatement.setString(2, cmd);
                    preparedStatement.setInt(3, uses);
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException exception) {
            LoggerUtils.error("Error creating vaultcher: " + exception.getMessage());
        }
    }

    @Override
    public boolean exists(@NotNull String name) {
        String query = "SELECT * FROM vaultcher WHERE VAULTCHER = ?";

        try {
            if (!getConnection().isValid(2)) reconnect();

            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setString(1, name);

                ResultSet resultSet = preparedStatement.executeQuery();
                return resultSet.next();
            }
        } catch (SQLException exception) {
            LoggerUtils.error("Error checking if vaultcher exists: " + exception.getMessage());
            return false;
        }
    }

    @Override
    public void redeemVaultcher(@NotNull String name, @NotNull OfflinePlayer player) {
        String selectQuery = "SELECT USES, COMMAND, OWNERS FROM vaultcher WHERE VAULTCHER = ?";
        String updateQuery = "UPDATE vaultcher SET USES = USES - 1 WHERE VAULTCHER = ?";
        String deleteQuery = "DELETE FROM vaultcher WHERE VAULTCHER = ?";
        String updateOwnersQuery = "UPDATE vaultcher SET OWNERS = REPLACE(REPLACE(',' || OWNERS || ',', ', ' || ?, ', '), ', ', ', ') WHERE VAULTCHER = ?";

        try {
            int currentUses = 0;
            String command = "";

            try (PreparedStatement selectStatement = getConnection().prepareStatement(selectQuery)) {
                selectStatement.setString(1, name);

                ResultSet resultSet = selectStatement.executeQuery();
                if (resultSet.next()) {
                    currentUses = resultSet.getInt("USES");
                    command = resultSet.getString("COMMAND");
                }
            }

            if (currentUses <= 0) {
                try (PreparedStatement deleteStatement = getConnection().prepareStatement(deleteQuery)) {
                    deleteStatement.setString(1, name);
                    deleteStatement.executeUpdate();
                }
            } else {
                try (PreparedStatement updateStatement = getConnection().prepareStatement(updateQuery)) {
                    updateStatement.setString(1, name);
                    updateStatement.executeUpdate();
                }

                try (PreparedStatement updateOwnersStatement = getConnection().prepareStatement(updateOwnersQuery)) {
                    updateOwnersStatement.setString(1, player.getName());
                    updateOwnersStatement.setString(2, name);
                    updateOwnersStatement.executeUpdate();
                }

                if (!command.isEmpty()) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", Objects.requireNonNull(player.getName())));
                }
            }
        } catch (SQLException exception) {
            LoggerUtils.error("Error redeeming vaultcher: " + exception.getMessage());
        }
    }

    @Override
    public void giveVaultcher(@NotNull String vaultcher, @NotNull OfflinePlayer player) {
        String updateOwnersQuery = "UPDATE vaultcher SET OWNERS = IFNULL(OWNERS || ', ', '') || ? WHERE VAULTCHER = ?";

        try {
            if (vaultcher.isEmpty() || Objects.requireNonNull(player.getName()).isEmpty()) {
                LoggerUtils.error("Invalid player name or vaultcher provided.");
                return;
            }

            try (PreparedStatement updateOwnersStatement = getConnection().prepareStatement(updateOwnersQuery)) {
                updateOwnersStatement.setString(1, player.getName());
                updateOwnersStatement.setString(2, vaultcher);
                updateOwnersStatement.executeUpdate();
            }
        } catch (SQLException exception) {
            LoggerUtils.error("Error giving vaultcher: " + exception.getMessage());
        }
    }

    @Override
    public boolean isOwned(@NotNull String vaultcher, @NotNull OfflinePlayer player) {
        String selectQuery = "SELECT OWNERS FROM vaultcher WHERE VAULTCHER = ?";

        try {
            try (PreparedStatement selectStatement = getConnection().prepareStatement(selectQuery)) {
                selectStatement.setString(1, vaultcher);

                ResultSet resultSet = selectStatement.executeQuery();
                if (resultSet.next()) {
                    String ownersList = resultSet.getString("OWNERS");
                    return ownersList != null && ownersList.contains(Objects.requireNonNull(player.getName()));
                }
            }
        } catch (SQLException exception) {
            LoggerUtils.error("Error checking ownership: " + exception.getMessage());
        }

        return false;
    }

    @Override
    public boolean isUsesZero(@NotNull String vaultcher) {
        String query = "SELECT USES FROM vaultcher WHERE VAULTCHER = ?";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setString(1, vaultcher);

                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    int uses = resultSet.getInt("USES");
                    return uses == 0;
                }
            }
        } catch (SQLException exception) {
            LoggerUtils.error("Error checking uses: " + exception.getMessage());
        }

        return false;
    }

    @Override
    public int getUses(@NotNull String vaultcher) {
        String query = "SELECT USES FROM vaultcher WHERE VAULTCHER = ?";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setString(1, vaultcher);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) return resultSet.getInt("USES");
            }
        } catch (SQLException exception) {
            LoggerUtils.error("Error getting uses: " + exception.getMessage());
        }

        return 0;
    }

    @Override
    public String getCommand(@NotNull String vaultcher) {
        String query = "SELECT COMMAND FROM vaultcher WHERE VAULTCHER = ?";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setString(1, vaultcher);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) return resultSet.getString("COMMAND");
            }
        } catch (SQLException exception) {
            LoggerUtils.error("Error getting command: " + exception.getMessage());
        }

        return "";
    }

    @Override
    public String getName(@NotNull String vaultcher) {
        String query = "SELECT VAULTCHER FROM vaultcher WHERE VAULTCHER = ?";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setString(1, vaultcher);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) return resultSet.getString("VAULTCHER");
            }
        } catch (SQLException exception) {
            LoggerUtils.error("Error getting vaultcher name: " + exception.getMessage());
        }

        return "";
    }

    @Override
    public void takeVaultcher(@NotNull String vaultcher, @NotNull String oldOwner, @NotNull String newOwner) {
        String query = "SELECT OWNERS FROM vaultcher WHERE VAULTCHER = ?";
        String updateQuery = "UPDATE vaultcher SET OWNERS = ? WHERE VAULTCHER = ?";

        try (PreparedStatement selectStatement = getConnection().prepareStatement(query)) {
            selectStatement.setString(1, vaultcher);
            ResultSet resultSet = selectStatement.executeQuery();
            if (resultSet.next()) {
                String owners = resultSet.getString("OWNERS");
                List<String> ownerList = new ArrayList<>(Arrays
                        .stream(owners.split(","))
                        .map(String::trim)
                        .toList());

                int index = ownerList.indexOf(oldOwner);

                if (index != -1) {
                    ownerList.set(index, newOwner);
                    String updatedOwners = String.join(", ", ownerList);

                    try (PreparedStatement updateStatement = getConnection().prepareStatement(updateQuery)) {
                        updateStatement.setString(1, updatedOwners);
                        updateStatement.setString(2, vaultcher);
                        updateStatement.executeUpdate();
                    }
                }
            }
        } catch (SQLException exception) {
            LoggerUtils.error("Error transferring ownership: " + exception.getMessage());
        }
    }

    @Override
    public void deleteVaultcher(@NotNull String vaultcher) {
        String deleteQuery = "DELETE FROM vaultcher WHERE VAULTCHER = ?";

        try {
            try (PreparedStatement deleteStatement = getConnection().prepareStatement(deleteQuery)) {
                deleteStatement.setString(1, vaultcher);
                deleteStatement.executeUpdate();
            }
        } catch (SQLException exception) {
            LoggerUtils.error("Error deleting vaultcher: " + exception.getMessage());
        }
    }

    @Override
    public void changeName(@NotNull String oldName, @NotNull String newName) {
        String updateQuery = "UPDATE vaultcher SET VAULTCHER = ? WHERE VAULTCHER = ?";

        try {
            try (PreparedStatement updateStatement = getConnection().prepareStatement(updateQuery)) {
                updateStatement.setString(1, newName);
                updateStatement.setString(2, oldName);
                updateStatement.executeUpdate();
            }
        } catch (SQLException exception) {
            LoggerUtils.error("Error changing vaultcher name: " + exception.getMessage());
        }
    }

    @Override
    public void changeCommand(@NotNull String name, @NotNull String newCommand) {
        String updateQuery = "UPDATE vaultcher SET COMMAND = ? WHERE VAULTCHER = ?";

        try {
            try (PreparedStatement updateStatement = getConnection().prepareStatement(updateQuery)) {
                updateStatement.setString(1, newCommand);
                updateStatement.setString(2, name);
                updateStatement.executeUpdate();
            }
        } catch (SQLException exception) {
            LoggerUtils.error("Error changing command: " + exception.getMessage());
        }
    }

    @Override
    public void changeUses(@NotNull String name, int newUses) {
        String updateQuery = "UPDATE vaultcher SET USES = ? WHERE VAULTCHER = ?";

        try {
            try (PreparedStatement updateStatement = getConnection().prepareStatement(updateQuery)) {
                updateStatement.setInt(1, newUses);
                updateStatement.setString(2, name);
                updateStatement.executeUpdate();
            }
        } catch (SQLException exception) {
            LoggerUtils.error("Error changing uses: " + exception.getMessage());
        }
    }

    @Override
    public List<VaultcherData> getVaultchers(@NotNull OfflinePlayer player) {
        List<VaultcherData> vaultchers = new ArrayList<>();

        String query = "SELECT * FROM vaultcher WHERE USES >= 1 AND OWNERS LIKE ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, "%" + player.getName() + "%");

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String name = resultSet.getString("VAULTCHER");
                String command = resultSet.getString("COMMAND");
                int uses = resultSet.getInt("USES");

                vaultchers.add(new VaultcherData(name, command, uses));
            }
        } catch (SQLException exception) {
            LoggerUtils.error("Error getting vaultchers: " + exception.getMessage());
        }

        return vaultchers;
    }

    @Override
    public List<VaultcherData> getEveryVaultcher() {
        List<VaultcherData> vaultchers = new ArrayList<>();
        String query = ConfigKeys.USES_MUST_BE_BIGGER_THAN_ONE.getBoolean() ? "SELECT * FROM vaultcher WHERE USES >= 1" : "SELECT * FROM vaultcher";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String name = resultSet.getString("VAULTCHER");
                String command = resultSet.getString("COMMAND");
                int uses = resultSet.getInt("USES");

                vaultchers.add(new VaultcherData(name, command, uses));
            }
        } catch (SQLException exception) {
            LoggerUtils.error("Error getting every vaultcher: " + exception.getMessage());
        }

        return vaultchers;
    }

    @Override
    public void reconnect() {
        try {
            if (getConnection() != null && !getConnection().isClosed()) getConnection().close();
            new SQLite();
        } catch (SQLException | ClassNotFoundException exception) {
            LoggerUtils.error("Error reconnecting: " + exception.getMessage());
        }
    }
}
