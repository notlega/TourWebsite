package utils;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LoadSQLDriver {

	public static Connection loadSQLDriver() throws ClassNotFoundException, SQLException {
		Dotenv dotenv = Dotenv.configure().load();
		// Load JDBC Driver
		Class.forName(dotenv.get("JDBC_DRIVER"));
		// Open connection to database
		return DriverManager.getConnection(dotenv.get("DB_URI"), dotenv.get("SQLUser"), dotenv.get("SQLPassword"));
	}
}
