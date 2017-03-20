package wx.org.img;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

public class writeData {
	
	private static Connection conn = null;
	private PreparedStatement statement = null;
	
	
	
	public void showResult(ResultSet rs){
		try {
			while(rs.next()){
				String usr_mac = rs.getString("user_mac");
				String usr_ip = rs.getString("user_ip");
				String head_img = rs.getString("head_img");
				System.out.println("[writeData getConnection info] This user infor is: " + usr_mac +"\t" +usr_ip + "\t"+ head_img);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void getConnection() {
		
		String url = "jdbc:mysql://192.168.0.17:3306/user_info?useUnicode=true&characterEncoding=utf8";
		String name = "root";
		String pass = "123456";
				
		try {
			conn = (Connection) DriverManager.getConnection(url, name, pass);
			System.out.println("[writeData getConnection info] This program has connect to the mysql server");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void deconnSQL(){
		try {
			if(conn != null){
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ResultSet selectSQL(String sql){
		
		ResultSet rSet = null;
		try {
			statement = (PreparedStatement) conn.prepareStatement(sql);
			rSet = statement.executeQuery(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return rSet;

	}
	
	public boolean deleteSQL(String sql){
		
		try {
			statement = (PreparedStatement) conn.prepareStatement(sql);
			statement.execute();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public boolean insertSQL(String sql){
		
		try {
			statement = (PreparedStatement) conn.prepareStatement(sql);
			statement.execute();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public boolean updateSQL(String sql){
		try {
			statement = (PreparedStatement) conn.prepareStatement(sql);
			statement.execute();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	


}
