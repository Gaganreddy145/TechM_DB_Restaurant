package db;

import java.sql.*;

public class DbInsert {

	public static void main(String[] args) {
		String dbURL = "jdbc:mysql://127.0.0.1:3306/techmjdbc";
		String username = "root";
		String password = "gaganeswar145";

		try {
			// Load MySQL JDBC Driver
			Class.forName("com.mysql.cj.jdbc.Driver");

			// Connect to database
			Connection conn = DriverManager.getConnection(dbURL, username, password);

			if (conn != null) {
				String s = "INSERT INTO employee (ename, email) VALUES (?, ?),(?,?)";
				PreparedStatement ps = conn.prepareStatement(s);
				ps.setString(1, "Reddy");
				ps.setString(2, "red@example.com");
				ps.setString(3, "TAO");
				ps.setString(4, "tao@example.com");

				int res = ps.executeUpdate();
				if (res > 0) {
					System.out.println("Inserted");
				} else {
					System.out.println("Insertion Failed");
				}

				ps.close();
				conn.close();
			} else {
				System.out.println("Connection unsuccessful");
			}

		} catch (ClassNotFoundException e) {
			System.out.println("MySQL JDBC Driver not found!");
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("SQL Exception occurred!");
			e.printStackTrace();
		}
	}
}
