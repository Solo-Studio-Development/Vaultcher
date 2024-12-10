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
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Getter
public class MySQL extends AbstractDatabase {
    private final Connection connection;

    public MySQL(@NotNull ConfigurationSection section) throws SQLException {
        HikariConfig hikariConfig = new HikariConfig();

        String host = section.getString("host");
        String database = section.getString("database");
        String user = section.getString("username");
        String pass = section.getString("password");
        int port = section.getInt("port");
        boolean ssl = section.getBoolean("ssl");
        boolean certificateVerification = section.getBoolean("certificateverification");
        int poolSize = section.getInt("poolsize");
        int maxLifetime = section.getInt("lifetime");

        hikariConfig.setPoolName("CodePool");
        hikariConfig.setMaximumPoolSize(poolSize);
        hikariConfig.setMaxLifetime(maxLifetime * 1000L);
        hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        hikariConfig.setUsername(user);
        hikariConfig.setPassword(pass);
        hikariConfig.addDataSourceProperty("useSSL", String.valueOf(ssl));
        if (!certificateVerification)
            hikariConfig.addDataSourceProperty("verifyServerCertificate", String.valueOf(false));
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("encoding", "UTF-8");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("jdbcCompliantTruncation", "false");
        hikariConfig.addDataSourceProperty("characterEncoding", "utf8");
        hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
        hikariConfig.addDataSourceProperty("socketTimeout", String.valueOf(TimeUnit.SECONDS.toMillis(30)));
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "275");
        hikariConfig.addDataSourceProperty("useUnicode", "true");
        HikariDataSource dataSource = new HikariDataSource(hikariConfig);
        connection = dataSource.getConnection();
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
                LoggerUtils.error(exception.getMessage());
            }
        }
    }

    public void createTable() {
        String query = "CREATE TABLE IF NOT EXISTS code (CODE VARCHAR(255) NOT NULL, CMD VARCHAR(255) NOT NULL, USES INT, OWNERS VARCHAR(255), PRIMARY KEY (CODE))";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
            preparedStatement.execute();
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }
    }

    @Override
    public void createVaultcher(@NotNull String name, @NotNull String cmd, int uses) {
        String query = "INSERT IGNORE INTO code (CODE, CMD, USES) VALUES (?, ?, ?)";

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
        String query = "SELECT * FROM code WHERE CODE = ?";

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
        String selectQuery = "SELECT USES, CMD, OWNERS FROM code WHERE CODE = ?";
        String updateQuery = "UPDATE code SET USES = USES - 1 WHERE CODE = ?";
        String deleteQuery = "DELETE FROM code WHERE CODE = ?";
        String updateOwnersQuery = "UPDATE code SET OWNERS = TRIM(BOTH ', ' FROM REPLACE(CONCAT(', ', OWNERS, ', '), CONCAT(', ', ?, ', '), ', ')) WHERE CODE = ?";

        try {
            int currentUses = 0;
            String command = "";

            try (PreparedStatement selectStatement = getConnection().prepareStatement(selectQuery)) {
                selectStatement.setString(1, name);

                ResultSet resultSet = selectStatement.executeQuery();
                if (resultSet.next()) {
                    currentUses = resultSet.getInt("USES");
                    command = resultSet.getString("CMD");
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
    public void giveVaultcher(@NotNull String code, @NotNull OfflinePlayer player) {
        String updateOwnersQuery = "UPDATE code SET OWNERS = CONCAT(IFNULL(OWNERS,''), ?, ', ') WHERE CODE = ?";

        try {
            try (PreparedStatement updateOwnersStatement = getConnection().prepareStatement(updateOwnersQuery)) {
                updateOwnersStatement.setString(1, player.getName());
                updateOwnersStatement.setString(2, code);
                updateOwnersStatement.executeUpdate();
            }
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }
    }

    @Override
    public boolean isOwned(@NotNull String code, @NotNull OfflinePlayer player) {
        String selectQuery = "SELECT OWNERS FROM code WHERE CODE = ?";

        try {
            try (PreparedStatement selectStatement = getConnection().prepareStatement(selectQuery)) {
                selectStatement.setString(1, code);

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
    public boolean isUsesZero(@NotNull String code) {
        String query = "SELECT USES FROM code WHERE CODE = ?";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setString(1, code);

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
    public int getUses(@NotNull String code) {
        String query = "SELECT USES FROM code WHERE CODE = ?";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setString(1, code);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) return resultSet.getInt("USES");
            }
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }

        return 0;
    }

    @Override
    public String getCommand(@NotNull String code) {
        String query = "SELECT CMD FROM code WHERE CODE = ?";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setString(1, code);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) return resultSet.getString("CMD");
            }
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }

        return "";
    }

    @Override
    public String getName(@NotNull String code) {
        String query = "SELECT CODE FROM code WHERE CODE = ?";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setString(1, code);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) return resultSet.getString("CODE");
            }
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }

        return "";
    }

    @Override
    public void takeVaultcher(@NotNull String code, @NotNull String oldOwner, @NotNull String newOwner) {
        String query = "SELECT OWNERS FROM code WHERE CODE = ?";
        String updateQuery = "UPDATE code SET OWNERS = ? WHERE CODE = ?";

        try (PreparedStatement selectStatement = getConnection().prepareStatement(query)) {
            selectStatement.setString(1, code);
            ResultSet resultSet = selectStatement.executeQuery();
            if (resultSet.next()) {
                String owners = resultSet.getString("OWNERS");
                List<String> ownerList = Arrays
                        .stream(owners.split(","))
                        .map(String::trim)
                        .collect(Collectors.toList());

                int index = ownerList.indexOf(oldOwner);

                if (index != -1) {
                    ownerList.set(index, newOwner);
                    String updatedOwners = String.join(", ", ownerList);

                    try (PreparedStatement updateStatement = getConnection().prepareStatement(updateQuery)) {
                        updateStatement.setString(1, updatedOwners);
                        updateStatement.setString(2, code);
                        updateStatement.executeUpdate();
                    }
                }
            }
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }
    }

    @Override
    public void deleteVaultcher(@NotNull String code) {
        String deleteQuery = "DELETE FROM code WHERE CODE = ?";

        try {
            try (PreparedStatement deleteStatement = getConnection().prepareStatement(deleteQuery)) {
                deleteStatement.setString(1, code);
                deleteStatement.executeUpdate();
            }
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }
    }

    @Override
    public void changeName(@NotNull String oldName, @NotNull String newName) {
        String updateQuery = "UPDATE code SET CODE = ? WHERE CODE = ?";

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
        String updateQuery = "UPDATE code SET CMD = ? WHERE CODE = ?";

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
        String updateQuery = "UPDATE code SET USES = ? WHERE CODE = ?";

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
        List<VaultcherData> codes = new ArrayList<>();
        String query = "SELECT * FROM code WHERE USES >= 1 AND OWNERS LIKE ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, "%" + player.getName() + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString("CODE");
                String command = resultSet.getString("CMD");
                int uses = resultSet.getInt("USES");
                codes.add(new VaultcherData(name, command, uses));
            }
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }

        return codes;
    }

    @Override
    public List<VaultcherData> getEveryVaultcher() {
        List<VaultcherData> codes = new ArrayList<>();
        String query = ConfigKeys.USES_MUST_BE_BIGGER_THAN_ONE.getBoolean() ? "SELECT * FROM code WHERE USES >= 1" : "SELECT * FROM code";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String name = resultSet.getString("CODE");
                String command = resultSet.getString("CMD");
                int uses = resultSet.getInt("USES");
                codes.add(new VaultcherData(name, command, uses));
            }
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }

        return codes;
    }

    @Override
    public void reconnect() {
        try {
            if (getConnection() != null && !getConnection().isClosed()) getConnection().close();
            new MySQL(Objects.requireNonNull(Vaultcher.getInstance().getConfiguration().getSection("database.mysql")));
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }
    }
}
