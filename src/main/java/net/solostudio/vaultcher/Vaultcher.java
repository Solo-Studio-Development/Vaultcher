package net.solostudio.vaultcher;

import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import lombok.Getter;

import net.solostudio.vaultcher.config.Config;
import net.solostudio.vaultcher.database.AbstractDatabase;
import net.solostudio.vaultcher.database.MySQL;
import net.solostudio.vaultcher.database.H2;
import net.solostudio.vaultcher.enums.DatabaseTypes;
import net.solostudio.vaultcher.enums.LanguageTypes;
import net.solostudio.vaultcher.enums.keys.ConfigKeys;
import net.solostudio.vaultcher.language.Language;
import net.solostudio.vaultcher.utils.LoggerUtils;
import org.bstats.bukkit.Metrics;
import revxrsal.zapper.ZapperJavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Objects;

import static net.solostudio.vaultcher.hook.PlaceholderAPI.registerHook;
import static net.solostudio.vaultcher.update.SpigotUpdateFetcher.checkUpdates;
import static net.solostudio.vaultcher.utils.StartingUtils.*;

public final class Vaultcher extends ZapperJavaPlugin {
    @Getter private static Vaultcher instance;
    @Getter private TaskScheduler scheduler;
    @Getter private Language language;
    @Getter private static AbstractDatabase database;
    private Config config;

    @Override
    public void onLoad() {
        instance = this;
        scheduler = UniversalScheduler.getScheduler(this);
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        initializeComponents();
        initializeDatabaseManager();
        checkUpdates();

        try {
            initialize();
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | NoSuchMethodException | IllegalAccessException exception) {
            LoggerUtils.error(exception.getMessage());
        }

        new Metrics(this, 24109);
        registerHook();
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
        saveResourceIfNotExists("config.yml");

        language = new Language("messages_" + LanguageTypes.valueOf(ConfigKeys.LANGUAGE.getString()));
    }

    private void initializeDatabaseManager() {
        try {
            switch (DatabaseTypes.valueOf(ConfigKeys.DATABASE.getString().toUpperCase())) {
                case MYSQL -> {
                    LoggerUtils.info("### MySQL support found! Starting to initializing it... ###");
                    database = new MySQL(Objects.requireNonNull(getConfiguration().getSection("database.mysql")));
                    MySQL mySQL = (MySQL) database;

                    mySQL.createTable();
                    LoggerUtils.info("### MySQL database has been successfully initialized! ###");
                }
                case H2 -> {
                    LoggerUtils.info("### H2 support found! Starting to initializing it... ###");
                    database = new H2();
                    H2 h2 = (H2) database;

                    h2.createTable();
                    LoggerUtils.info("### H2 database has been successfully initialized! ###");
                }
                default -> throw new SQLException("Unsupported database type!");
            }
        } catch (SQLException | ClassNotFoundException exception) {
            LoggerUtils.error("Database initialization failed: {}", exception.getMessage());
        }
    }
}
