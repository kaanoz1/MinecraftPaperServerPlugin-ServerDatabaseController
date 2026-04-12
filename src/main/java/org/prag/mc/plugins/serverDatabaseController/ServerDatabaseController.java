package org.prag.mc.plugins.serverDatabaseController;

import org.bukkit.plugin.java.JavaPlugin;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.prag.mc.plugins.serverDatabaseController.Models.MarketChest;
import org.prag.mc.plugins.serverDatabaseController.Utils.AnsiConstants;
import org.prag.mc.plugins.serverDatabaseController.Models.RecordedPlayer;

import java.io.File;
import java.io.IOException;

public final class ServerDatabaseController extends JavaPlugin {

    private static SessionFactory sessionFactory;

    @Override
    public void onEnable() {
        this.ensureDirectoryExists();

        if (this.isDatabaseFileExist())
            getLogger().info(AnsiConstants.ANSI_GREEN + "Database file found. Initializing..." + AnsiConstants.ANSI_RESET);
        else {
            getLogger().warning("Database file not found! Creating a new one...");
            String createdPath = this.createDatabaseFile();
            getLogger().info(AnsiConstants.ANSI_GREEN + "Database file created! At: " + createdPath + AnsiConstants.ANSI_RESET);
        }

        this.initializeHibernate();

        getLogger().info(AnsiConstants.ANSI_GREEN + "DatabaseController is enabled!" + AnsiConstants.ANSI_RESET);
    }

    private void initializeHibernate() {
        try {
            Configuration configuration = new Configuration();

            configuration.addAnnotatedClass(RecordedPlayer.class);
            configuration.addAnnotatedClass(MarketChest.class);

            sessionFactory = configuration.buildSessionFactory();
            getLogger().info(AnsiConstants.ANSI_GREEN + "Hibernate SessionFactory initialized via properties!" + AnsiConstants.ANSI_RESET);
        } catch (Exception e) {
            getLogger().severe(AnsiConstants.ANSI_RED + "Hibernate failed: " + e.getMessage() + AnsiConstants.ANSI_RESET);
            throw new RuntimeException("Critical Hibernate initialization error", e);
        }
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null)
            throw new IllegalStateException(AnsiConstants.ANSI_RED + "SessionFactory is not initialized yet! Did you call this before onEnable finished?" + AnsiConstants.ANSI_RESET);

        return sessionFactory;
    }

    private void ensureDirectoryExists() {
        if (!getDataFolder().exists() && !getDataFolder().mkdirs())
            throw new RuntimeException(AnsiConstants.ANSI_RED + "Critical: Could not create plugin directory!" + AnsiConstants.ANSI_RESET);
    }

    private boolean isDatabaseFileExist() {
        File dbFile = new File(getDataFolder(), "database.db");
        return dbFile.exists();
    }

    private String createDatabaseFile() {
        File dbFile = new File(getDataFolder(), "database.db");
        try {
            if (dbFile.createNewFile())
                return dbFile.getAbsolutePath();
            else
                throw new RuntimeException("Failed to create database.db: File already exists but check failed?");

        } catch (IOException e) {
            throw new RuntimeException("Critical error while creating database.db file", e);
        }
    }

    @Override
    public void onDisable() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
            getLogger().info(AnsiConstants.ANSI_GREEN + "Hibernate SessionFactory closed." + AnsiConstants.ANSI_RESET);
        }
        getLogger().info(AnsiConstants.ANSI_GREEN + "DatabaseController is disabled!" + AnsiConstants.ANSI_RESET);
    }
}