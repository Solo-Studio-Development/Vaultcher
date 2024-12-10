package net.solostudio.vaultcher;

import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.solostudio.vaultcher.config.Config;
import net.solostudio.vaultcher.database.AbstractDatabase;
import net.solostudio.vaultcher.database.MySQL;
import net.solostudio.vaultcher.database.SQLite;
import net.solostudio.vaultcher.enums.DatabaseTypes;
import net.solostudio.vaultcher.enums.LanguageTypes;
import net.solostudio.vaultcher.enums.keys.ConfigKeys;
import net.solostudio.vaultcher.language.Language;
import net.solostudio.vaultcher.utils.LoggerUtils;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Objects;

import static net.solostudio.vaultcher.utils.StartingUtils.*;

public final class Vaultcher extends JavaPlugin {
    @Getter private static Vaultcher instance;
    @Getter private TaskScheduler scheduler;
    @Getter private Language language;
    @Getter private static AbstractDatabase database;
    private Config config;


    @Override
    public void onLoad() {
        instance = this;
        scheduler = UniversalScheduler.getScheduler(this);

        checkVersion();
    }

    @Override
    public void onEnable() {
        checkVM();
        saveDefaultConfig();
        initializeComponents();
        initializeDatabaseManager();
        registerListenersAndCommands();

        new Metrics(this, 24109);
    }

    @Override
    public void onDisable() {
        if (database != null) database.disconnect();
    }

    public Config getConfiguration() {
        return config;
    }

    private void initializeComponents() {
        config = new Config();

        saveResourceIfNotExists("locales/messages_en.yml");
        saveResourceIfNotExists("locales/messages_de.yml");

        language = new Language("messages_" + LanguageTypes.valueOf(ConfigKeys.LANGUAGE.getString()));
    }

    private void initializeDatabaseManager() {
        try {
            switch (DatabaseTypes.valueOf(ConfigKeys.DATABASE.getString())) {
                case MYSQL -> {
                    LoggerUtils.info("### MySQL support found! Starting to initializing it... ###");
                    database = new MySQL(Objects.requireNonNull(getConfiguration().getSection("database.mysql")));
                    MySQL mySQL = (MySQL) database;

                    mySQL.createTable();
                    LoggerUtils.info("### MySQL database has been successfully initialized! ###");
                }
                case SQLITE -> {
                    LoggerUtils.info("### SQLite support found! Starting to initializing it... ###");
                    database = new SQLite();
                    SQLite sqlite = (SQLite) database;

                    sqlite.createTable();
                    LoggerUtils.info("### SQLite database has been successfully initialized! ###");
                }
                default -> throw new SQLException("Unsupported database type!");
            }
        } catch (SQLException | ClassNotFoundException exception) {
            LoggerUtils.error("Database initialization failed: {}", exception.getMessage());
        }
    }
}
