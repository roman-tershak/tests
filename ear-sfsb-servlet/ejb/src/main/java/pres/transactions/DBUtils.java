package pres.transactions;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class DBUtils {

	public static int execute(Connection connection, String sql, Object... params) throws SQLException {
		PreparedStatement preparedStatement = prepareSql(connection, sql, params);
		logSql(sql, params);
		
		return preparedStatement.executeUpdate();
	}

	public static void executeQuery(Connection connection, String sql, Object... params) throws SQLException {
		PreparedStatement preparedStatement = prepareSql(connection, sql, params);
		
		ResultSet resultSet = preparedStatement.executeQuery();
		logSqlAndResults(sql, resultSet, params);
		
	}
	
	public static void cleanupDatabase() throws Exception {
		Class.forName("oracle.jdbc.OracleDriver");
		Connection oracleConn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "tests", "tests");
		oracleConn.createStatement().executeUpdate("DELETE FROM TABLE1");
		oracleConn.close();
		
		Class.forName("org.postgresql.Driver");
		Connection postgresConn = DriverManager.getConnection("jdbc:postgresql:tests", "tests", "tests");
		postgresConn.createStatement().executeUpdate("DELETE FROM public.\"PGTABLE1\"");
		postgresConn.close();
	}

	private static PreparedStatement prepareSql(Connection connection, String sql, Object... params) throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		
		for (int i = 0; i < params.length; i++) {
			Object param = params[i];
			if (String.class.isInstance(param)) {
				preparedStatement.setString(i + 1, (String) param); 
			} else if (Integer.class.isInstance(param)) {
				preparedStatement.setInt(i + 1, (Integer) param); 
			}
		}
		return preparedStatement;
	}

	private static void logSql(String sql, Object... params) {
		System.out.println(printSqlWithParams(sql, params));
	}

	private static void logSqlAndResults(String sql, ResultSet resultSet, Object... params) throws SQLException {
		StringBuffer sb = new StringBuffer();
		sb.append(printSqlWithParams(sql, params));
		
		int columnCount = resultSet.getMetaData().getColumnCount();
		sb.append("Results:\n");
		while (resultSet.next()) {
			for (int i = 1; i <= columnCount; i++) {
				sb.append("\t").append(resultSet.getObject(i));
			}
			sb.append("\n");
		}
		sb.append("\n ");
		
		System.out.println(sb.toString());
	}

	private static String printSqlWithParams(String sql, Object... params) {
		StringBuffer sb = new StringBuffer("        ").append("Executing SQL:\n\n\t").append(sql);
		if (params.length > 0) {
			sb.append(" with params ").append(Arrays.toString(params));
		}
		sb.append("\n ");
		return sb.toString();
	}

	public static void closeDbConnection(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static String convertTransactionStatus(int status) {
		switch (status) {
		case 0:
			return "STATUS_ACTIVE";
		case 1:
			return "STATUS_MARKED_ROLLBACK";
		case 2:
			return "STATUS_PREPARED";
		case 3:
			return "STATUS_COMMITTED";
		case 4:
			return "STATUS_ROLLEDBACK";
		case 5:
			return "STATUS_UNKNOWN";
		case 6:
			return "STATUS_NO_TRANSACTION";
		case 7:
			return "STATUS_PREPARING";
		case 8:
			return "STATUS_COMMITTING";
		case 9:
			return "STATUS_ROLLING_BACK";
		default:
			throw new IllegalArgumentException("Not valid transaction status - " + status);
		}
		
	}
}
