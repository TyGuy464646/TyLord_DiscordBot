package me.TyGuy464646.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteDataSource {

	private static final Logger LOGGER = LoggerFactory.getLogger(SQLiteDataSource.class);

	private static final HikariConfig config = new HikariConfig();
	private static final HikariDataSource dataSource;

	static {
		try {
			final File dbFile = new File("database.db");

			if (!dbFile.exists()) {
				if (dbFile.createNewFile())
					LOGGER.info("Created database file.");
				else
					LOGGER.info("Could not create database file.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		config.setJdbcUrl("jdbc:sqlite:database.db");
		config.setConnectionTestQuery("SELECT 1");
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

		dataSource = new HikariDataSource(config);

		try (Statement statement = getConnection().createStatement()) {

			// language=SQLite
			statement.execute("CREATE TABLE IF NOT EXISTS guild_settings (" +
					"id INTEGER PRIMARY KEY AUTOINCREMENT," +
					"guild_id VARCHAR(20) NOT NULL" +
					");");
			//
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private SQLiteDataSource() {
	}

	public static Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}
}
