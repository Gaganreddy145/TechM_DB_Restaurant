package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DbSelect {
	private List<Employeee> le;

	DbSelect() {
		le = new ArrayList<>();
	}

	public List<Employeee> getLe() {
		return le;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String DB_URL = "jdbc:mysql://127.0.0.1:3306/techmjdbc";
		String DB_USERNAME = "root";
		String DB_PASSWORD = "gaganeswar145";
		DbSelect db = new DbSelect();
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
			if (conn != null) {
				String sqlQuery = "select * from employee";
				PreparedStatement ps = conn.prepareStatement(sqlQuery);
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					db.le.add(new Employeee(rs.getInt("eid"), rs.getString("ename"), rs.getString("email")));
				}
				PreparedStatement ps1 = conn.prepareStatement("select eid from employee");
				ResultSet rs1 = ps1.executeQuery();
				while (rs1.next()) {
					System.out.println(rs1.getInt("eid"));
				}
				ps.close();
				conn.close();
			} else {
				System.out.println("Connection failure");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (Employeee e : db.le) {
			System.out.println(e.getEid() + " " + e.getName() + " " + e.getEmail());
		}
	}

}
