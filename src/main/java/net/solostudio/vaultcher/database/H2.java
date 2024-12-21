package net.solostudio.vaultcher.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import net.solostudio.vaultcher.Vaultcher;
import net.solostudio.vaultcher.enums.keys.ConfigKeys;
import net.solostudio.vaultcher.managers.VaultcherData;
import net.solostudio.vaultcher.utils.LoggerUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class H2 extends AbstractDatabase {
    private final Connection connection;
    private final HikariDataSource dataSource;

    public H2() throws SQLException, ClassNotFoundException {
        Class.forName("net.solostudio.vaultcher.libs.h2.Driver");

        HikariConfig hikariConfig = new HikariConfig();

        String url = "jdbc:h2:" + Vaultcher.getInstance().getDataFolder().getAbsolutePath() + "/database;MODE=MySQL";

        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername("sa");
        hikariConfig.setPassword("");
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setPoolName("VaultcherPool");

        dataSource = new HikariDataSource(hikariConfig);
        connection = dataSource.getConnection();
    }

    @Override
    public boolean isConnected() {
        return getConnection() != null;
    }

    @Override
    public void disconnect() {
        if (isConnected()) {
            try {
                getConnection().close();
            } catch (SQLException exception) {
                LoggerUtils.error(exception.getMessage());
            }
        }
    }

    @Override
    public void createTable() {
        String vaultcherTableQuery = "CREATE TABLE IF NOT EXISTS vaultcher (VAULTCHER VARCHAR(255) NOT NULL, COMMAND VARCHAR(255) NOT NULL, USES INT, OWNERS VARCHAR(255), PRIMARY KEY (VAULTCHER))";
        String vaultcherPlayersTableQuery = "CREATE TABLE IF NOT EXISTS vaultcherplayers (NAME VARCHAR(255) NOT NULL, REFERRALCODE VARCHAR(7) NOT NULL, ACTIVATED BOOLEAN NOT NULL DEFAULT FALSE, ACTIVATORS INT NOT NULL DEFAULT 0, PRIMARY KEY (NAME))";

        try (PreparedStatement vaultcherTableStatement = getConnection().prepareStatement(vaultcherTableQuery)) {
            vaultcherTableStatement.execute();
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }

        try (PreparedStatement vaultcherPlayersTableStatement = getConnection().prepareStatement(vaultcherPlayersTableQuery)) {
            vaultcherPlayersTableStatement.execute();
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }
    }

    @Override
    public void createPlayer(@NotNull String playerName) {
        String query = "INSERT IGNORE INTO vaultcherplayers (NAME, REFERRALCODE, ACTIVATED, ACTIVATORS) VALUES (?, '', FALSE, 0)";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setString(1, playerName);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException exception) {
            LoggerUtils.error("Error creating player " + playerName + ": " + exception.getMessage());
        }
    }

    @Override
    public void createReferralCode(@NotNull String name, @NotNull String referralCode) {
        String query = "UPDATE vaultcherplayers SET REFERRALCODE = ? WHERE NAME = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, referralCode);
            preparedStatement.setString(2, name);
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            LoggerUtils.error("Error creating referral code for player " + name + ": " + exception.getMessage());
        }
    }


    @Override
    public boolean doesPlayerExists(@NotNull String name) {
        String query = "SELECT 1 FROM vaultcherplayers WHERE NAME = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();
        } catch (SQLException exception) {
            LoggerUtils.error("Error checking existence of player " + name + ": " + exception.getMessage());
        }
        return false;
    }

    @Override
    public String generateSafeCode() {
        SecureRandom random = new SecureRandom();
        String code;

        do {
            code = random.ints(48, 123)
                    .filter(i -> (i <= 57) || (i >= 65 && i <= 90) || (i >= 97 && i <= 122))
                    .limit(7)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
        } while (doesReferralCodeExist(code));

        return code;
    }

    @Override
    public boolean doesReferralCodeExist(String code) {
        String query = "SELECT 1 FROM vaultcherplayers WHERE REFERRALCODE = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, code);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException exception) {
            LoggerUtils.error("Error checking referral code existence: " + exception.getMessage());
        }
        return false;
    }

    @Override
    public void activateReferral(@NotNull String name) {
        String selectQuery = "SELECT ACTIVATED FROM vaultcherplayers WHERE NAME = ?";
        String updateQuery = "UPDATE vaultcherplayers SET ACTIVATED = TRUE WHERE NAME = ?";

        try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
             PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {

            selectStatement.setString(1, name);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                updateStatement.setString(1, name);
                updateStatement.executeUpdate();
            }
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }
    }

    @Override
    public void incrementActivators(@NotNull String referralCode) {
        String query = "UPDATE vaultcherplayers SET ACTIVATORS = ACTIVATORS + 1 WHERE REFERRALCODE = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, referralCode);
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }
    }

    @Override
    public int getActivators(@NotNull String referralCode) {
        String query = "SELECT ACTIVATORS FROM vaultcherplayers WHERE REFERRALCODE = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, referralCode);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) return resultSet.getInt("ACTIVATORS");
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }
        return 0;
    }

    @Override
    public int getActivatorsFromPlayer(@NotNull String name) {
        String query = "SELECT ACTIVATORS FROM vaultcherplayers WHERE NAME = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) return resultSet.getInt("ACTIVATORS");
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }
        return 0;
    }

    @Override
    public boolean isReferralActivated(@NotNull String name) {
        String query = "SELECT ACTIVATED FROM vaultcherplayers WHERE NAME = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) return resultSet.getBoolean("ACTIVATED");
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }
        return false;
    }

    @Override
    public String getReferralCodeOwner(@NotNull String referralCode) {
        String query = "SELECT NAME FROM vaultcherplayers WHERE REFERRALCODE = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, referralCode);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) return resultSet.getString("NAME");
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }
        return null;
    }

    @Override
    public String getReferralCode(@NotNull String name) {
        String query = "SELECT REFERRALCODE FROM vaultcherplayers WHERE NAME = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) return resultSet.getString("REFERRALCODE");
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }
        return null;
    }

    @Override
    public void createVaultcher(@NotNull String name, @NotNull String cmd, int uses) {
        String query = "INSERT IGNORE INTO vaultcher (VAULTCHER, COMMAND, USES) VALUES (?, ?, ?)";

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
            LoggerUtils.error(exception.getMessage());
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
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void redeemVaultcher(@NotNull String name, @NotNull OfflinePlayer player) {
        String selectQuery = "SELECT USES, COMMAND, OWNERS FROM vaultcher WHERE VAULTCHER = ?";
        String updateQuery = "UPDATE vaultcher SET USES = USES - 1 WHERE VAULTCHER = ?";
        String deleteQuery = "DELETE FROM vaultcher WHERE VAULTCHER = ?";
        String updateOwnersQuery = "UPDATE vaultcher\n" +
                "SET OWNERS = TRIM(BOTH ', ' FROM REPLACE(OWNERS, ?, ''))\n" +
                "WHERE VAULTCHER = ?";

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

                if (!command.isEmpty()) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", Objects.requireNonNull(player.getName())));
            }
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }
    }

    @Override
    public void giveVaultcher(@NotNull String vaultcher, @NotNull OfflinePlayer player) {
        String updateOwnersQuery = "UPDATE vaultcher SET OWNERS = CONCAT(IFNULL(OWNERS,''), ?, ', ') WHERE VAULTCHER = ?";

        try {
            try (PreparedStatement updateOwnersStatement = getConnection().prepareStatement(updateOwnersQuery)) {
                updateOwnersStatement.setString(1, player.getName());
                updateOwnersStatement.setString(2, vaultcher);
                updateOwnersStatement.executeUpdate();
            }
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
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
            LoggerUtils.error(exception.getMessage());
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
            LoggerUtils.error(exception.getMessage());
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
            LoggerUtils.error(exception.getMessage());
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
            LoggerUtils.error(exception.getMessage());
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
            LoggerUtils.error(exception.getMessage());
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
                List<String> ownerList = Arrays
                        .stream(resultSet.getString("OWNERS").split(","))
                        .map(String::trim)
                        .collect(Collectors.toList());

                int index = ownerList.indexOf(oldOwner);

                if (index != -1) {
                    ownerList.set(index, newOwner);

                    try (PreparedStatement updateStatement = getConnection().prepareStatement(updateQuery)) {
                        updateStatement.setString(1, String.join(", ", ownerList));
                        updateStatement.setString(2, vaultcher);
                        updateStatement.executeUpdate();
                    }
                }
            }
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
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
            LoggerUtils.error(exception.getMessage());
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
            LoggerUtils.error(exception.getMessage());
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
            LoggerUtils.error(exception.getMessage());
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
            LoggerUtils.error(exception.getMessage());
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
            LoggerUtils.error(exception.getMessage());
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
            LoggerUtils.error(exception.getMessage());
        }

        return vaultchers;
    }

    @Override
    public void reconnect() {
        try {
            if (getConnection() != null && !getConnection().isClosed()) getConnection().close();
            new H2();
        } catch (SQLException | ClassNotFoundException exception) {
            LoggerUtils.error(exception.getMessage());
        }
    }
}
