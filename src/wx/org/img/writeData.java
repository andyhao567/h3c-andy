package wx.org.img;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

public class writeData {
	
	private static Connection conn = null;
	private PreparedStatement statement = null;
	
	
	
	public Map showResult(ResultSet rs){
		Map<String, String> rsMap = new HashMap<String, String>();
		try {
			while(rs.next()){
				String usr_mac = rs.getString("mac_addr");
				String usr_ip = rs.getString("local_ip");
				String head_img = rs.getString("wechat_head_img");
				//System.out.println("[writeData getConnection info] This user infor is: " + usr_mac +"\t" +usr_ip + "\t"+ head_img);
				rsMap.put("usr_mac", usr_mac);
				rsMap.put("usr_ip", usr_ip);
				rsMap.put("head_img", head_img);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rsMap;
	}
	
	
	public static void getConnection() {
		
		String url = "jdbc:mysql://127.0.0.1:3306/sheepwall?useUnicode=true&characterEncoding=utf8";
		String name = "root";
		String pass = "789uio@jkl";
				
		try {
			conn = (Connection) DriverManager.getConnection(url, name, pass);
			System.out.println("[writeData getConnection info] This program has connect to the mysql server");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void getConnection(String geturl) {
		
		String url = geturl;
		String name = "root";
		String pass = "789uio@jkl";
				
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
				System.out.println("[writeData deconnSQL info] This program has deconnect to the mysql server");
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
