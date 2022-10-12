package utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public abstract class SQLQuery<T> {

	private final String query;
	private final boolean querySelect;

	public SQLQuery(String query) {
		this.query = query;
		this.querySelect = query.toLowerCase().startsWith("select");
	}

	private int getNumRowsModified(Connection connection, String[] parameters) {
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			for (int i = 0; i < parameters.length; i++) {
				preparedStatement.setString(i + 1, parameters[i]);
			}

			return preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return 0;
	}

	private ResultSet getResultSet(Connection connection, String[] parameters) {
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			for (int i = 0; i < parameters.length; i++) {
				preparedStatement.setString(i + 1, parameters[i]);
			}

			return preparedStatement.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public ArrayList<T> query(Connection connection, String[] parameters) {
		ResultSet resultSet = getResultSet(connection, parameters);
		ArrayList<T> results = new ArrayList<>();

		if (resultSet == null) {
			return null;
		}

		try {
			while (resultSet.next()) {
				results.add(parseResult(resultSet, 0));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return results;
	}

	public T querySingle(Connection connection, String[] parameters) {
		ResultSet resultSet = null;
		int numRowsModified = 0;

		if (!querySelect) {
			numRowsModified = getNumRowsModified(connection, parameters);
		} else {
			resultSet = getResultSet(connection, parameters);
		}

		if (resultSet == null && numRowsModified == 0) {
			return null;
		}

		try {
			return parseResult(resultSet, numRowsModified);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public ArrayList<T> query(Connection connection) {
		return query(connection, new String[]{});
	}

	public T querySingle(Connection connection) {
		return querySingle(connection, new String[]{});
	}

	protected abstract T parseResult(ResultSet resultSet, int numRowsModified) throws SQLException;
}
