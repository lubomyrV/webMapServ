package com.example.demo.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.json.JSONObject;

public class Sqlite {
	
	private String fileName;
	
	public Sqlite(String fileName) {
		this.fileName = fileName;
	}

    public int insertLocation(String jsonObj) {
        String sql = "INSERT INTO location (ip,country_name,state_prov,district,city,isp,latitude,longitude)"
        		+" VALUES(?,?,?,?,?,?,?,?)";
        JSONObject obj = new JSONObject(jsonObj);
        String ip = null;
		if(obj.has("ip") && obj.get("ip") != null) {
			ip = String.valueOf(obj.get("ip"));
		}
		String country = null;
		if(obj.has("country_name")) {
			country = String.valueOf(obj.get("country_name"));
		}
		String state_prov = null;
		if(obj.has("state_prov")) {
			state_prov = String.valueOf(obj.get("state_prov"));
		}
		String district = null;
		if(obj.has("district")) {
			district = String.valueOf(obj.get("district"));
		}			
		String city = null;
		if(obj.has("city")) {
			city = String.valueOf(obj.get("city"));
		}
		String isp = null;
		if(obj.has("isp")) {
			isp = String.valueOf(obj.get("isp"));
		}
		String latitude = null;
		if(obj.has("latitude")) {
			latitude = String.valueOf(obj.get("latitude"));
		}
		String longitude = null;
		if(obj.has("longitude")) {
			longitude = String.valueOf(obj.get("longitude"));
		}
        int result = 0;
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1,ip);
            pstmt.setString(2,country);
            pstmt.setString(3,state_prov);
            pstmt.setString(4,district);
            pstmt.setString(5,city);
            pstmt.setString(6,isp);
            pstmt.setString(7,latitude);
            pstmt.setString(8,longitude);
            result = pstmt.executeUpdate();
            System.out.println(result+" location insert "+ip);
        } catch (SQLException e) {
            System.out.println("SQLException "+e.getMessage());
        }
        return result;
    }
    
    public int insertClient(String jsonObj) {
        String sql = "INSERT INTO client ("
        		+ "remoteAddr,utcTime,query,accept_language,host,upgrade_insecure_requests,connection,dnt,"
        		+ "accept_encoding,user_agent,accept,content_length,pragma,referer,cache_control)"
        		+" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        JSONObject obj = new JSONObject(jsonObj);
        String remoteAddr = null;
		if(obj.has("remoteAddr")) {
			remoteAddr = String.valueOf(obj.get("remoteAddr"));
		}
		String ut = null;
		if(obj.has("utcTime")) {
			ut = String.valueOf(obj.get("utcTime"));
		}
		String qr = null;
		if(obj.has("query")) {
			qr = String.valueOf(obj.get("query"));
		}
		String al = null;
		if(obj.has("accept-language")) {
			al = String.valueOf(obj.get("accept-language"));
		}			
		String host = null;
		if(obj.has("host")) {
			host = String.valueOf(obj.get("host"));
		}
		String uir = null;
		if(obj.has("upgrade-insecure-requests")) {
			uir = String.valueOf(obj.get("upgrade-insecure-requests"));
		}
		String con = null;
		if(obj.has("connection")) {
			con = String.valueOf(obj.get("connection"));
		}
		String dnt = null;
		if(obj.has("dnt")) {
			dnt = String.valueOf(obj.get("dnt"));
		}
		String ae = null;
		if(obj.has("accept-encoding")) {
			ae = String.valueOf(obj.get("accept-encoding"));
		}
		String ua = null;
		if(obj.has("user-agent")) {
			ua = String.valueOf(obj.get("user-agent"));
		}
		String accept = null;
		if(obj.has("accept")) {
			accept = String.valueOf(obj.get("accept"));
		}
		String cl = null;
		if(obj.has("content-length")) {
			cl = String.valueOf(obj.get("content-length"));
		}
		String pragma = null;
		if(obj.has("pragma")) {
			pragma = String.valueOf(obj.get("pragma"));
		}
		String referer = null;
		if(obj.has("referer")) {
			referer = String.valueOf(obj.get("referer"));
		}
		String cc = null;
		if(obj.has("cache-control")) {
			cc = String.valueOf(obj.get("cache-control"));
		}
        int result = 0;
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1,remoteAddr);
            pstmt.setString(2,ut);
            pstmt.setString(3,qr);
            pstmt.setString(4,al);
            pstmt.setString(5,host);
            pstmt.setString(6,uir);
            pstmt.setString(7,con);
            pstmt.setString(8,dnt);
            pstmt.setString(9,ae);
            pstmt.setString(10,ua);
            pstmt.setString(11,accept);
            pstmt.setString(12,cl);
            pstmt.setString(13,pragma);
            pstmt.setString(14,referer);
            pstmt.setString(15,cc);
            
            result = pstmt.executeUpdate();
            System.out.println(result + " client insert "+remoteAddr);
        } catch (SQLException e) {
            System.out.println("SQLException "+e.getMessage());
        }
        return result;
    }
    
    public Set<String> getAllAddr(){
        String sql = "SELECT remoteAddr FROM client";
        Set<String> addrs = new HashSet<>();
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            
            // loop through the result set
            while (rs.next()) {
                addrs.add(rs.getString("remoteAddr"));
            }
        } catch (SQLException e) {
            System.out.println("SQLException "+e.getMessage());
        }
        return addrs;
    }
    
    public Set<String> getAllIPs(){
        String sql = "SELECT ip FROM location";
        Set<String> addrs = new HashSet<>();
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            
            // loop through the result set
            while (rs.next()) {
                addrs.add(rs.getString("ip"));
            }
        } catch (SQLException e) {
            System.out.println("SQLException "+e.getMessage());
        }
        return addrs;
    }
	
    public String getLocationIdByAddr(String addr){
        String sql = "SELECT id FROM location WHERE ip = ?";
        String result = null;
        try (Connection conn = this.connect();
		      PreparedStatement pstmt  = conn.prepareStatement(sql)){
			 // set the value
			 pstmt.setString(1,addr);
			 ResultSet rs  = pstmt.executeQuery();
			 // loop through the result set
			 while (rs.next()) {
				 result = rs.getString("id");
		     }
		 } catch (SQLException e) {
		     System.out.println(e.getMessage());
		 }
        return result;
	}
    
    public List<JSONObject> getAllClients() {
    	String sql = "SELECT query, remoteAddr, utcTime, upgrade_insecure_requests FROM client";
        List<JSONObject> jsonObjs = new ArrayList<JSONObject>();
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            // loop through the result set
            while (rs.next()) {
            	JSONObject obj = new JSONObject();
            	if(rs.getString("query") != null) {
            		obj.put("query", rs.getString("query"));
            	}
            	if(rs.getString("upgrade_insecure_requests") != null) {
            		obj.put("upgrade_insecure_requests", rs.getString("upgrade_insecure_requests"));
            	}
                obj.put("remoteAddr", rs.getString("remoteAddr"));
                obj.put("utcTime", rs.getString("utcTime"));
                jsonObjs.add(obj);
            }
        } catch (SQLException e) {
            System.out.println("SQLException "+e.getMessage());
        }
        return jsonObjs;
	}
    
    public List<JSONObject> getAllLocation(){
        String sql = "SELECT * FROM location";
        List<JSONObject> jsonObjs = new ArrayList<JSONObject>();
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            // loop through the result set
            while (rs.next()) {
            	JSONObject obj = new JSONObject();
            	obj.put("ip", rs.getString("ip"));
                obj.put("country_name", rs.getString("country_name"));
                obj.put("state_prov", rs.getString("state_prov"));
                obj.put("district", rs.getString("district"));
                obj.put("city", rs.getString("city"));
                obj.put("isp", rs.getString("isp"));
                obj.put("latitude", rs.getString("latitude"));
                obj.put("longitude", rs.getString("longitude"));
                jsonObjs.add(obj);
            }
        } catch (SQLException e) {
            System.out.println("SQLException "+e.getMessage());
        }
        return jsonObjs;
	}
	
	public void createNewDatabase() {
		
		String path = System.getProperty("user.dir") + File.separator + this.fileName;
		
		if(!new File(path).exists()) { 
			String url = "jdbc:sqlite:"+path;
			
			try (Connection conn = DriverManager.getConnection(url)) {
				if (conn != null) {
					DatabaseMetaData meta = conn.getMetaData();
					System.out.println("The driver name is " + meta.getDriverName());
					System.out.println("A new "+fileName+" database has been created.");
					
					this.createClientTable(fileName);
					this.createLocationTable(fileName);
				}
				
			} catch (SQLException e) {
				System.out.println("SQLException "+e.getMessage());
			}
	    }

    }

	private void createLocationTable(String fileName) {
        // SQLite connection string
        String url = "jdbc:sqlite:"+System.getProperty("user.dir") + File.separator + fileName;
        
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS location (\n"
                + "	id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "	ip TEXT NOT NULL UNIQUE,\n"
                + "	country_name TEXT,\n"
                + "	state_prov TEXT,\n"
                + "	district TEXT,\n"
                + "	city TEXT,\n"
                + "	isp TEXT,\n"
                + "	latitude TEXT,\n"
                + "	longitude TEXT \n"
                + ");";
        String idx = "CREATE INDEX idx_location_ip \n" + 
        		" ON location (ip);";
        try (Connection conn = DriverManager.getConnection(url); Statement stmt = conn.createStatement();) {
            // create a new table
            stmt.execute(sql);
            stmt.execute(idx);
            System.out.println("location table has been created.");
        } catch (SQLException e) {
            System.out.println("createLocationTable SQLException "+e.getMessage());
        }
    }
	
	private void createClientTable(String fileName) {
        // SQLite connection string
        String url = "jdbc:sqlite:"+System.getProperty("user.dir") + File.separator + fileName;
        
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS client (\n"
                + "	id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "	remoteAddr TEXT,\n"
                + " utcTime TEXT,\n"
                + " query TEXT,\n"
                + "	accept_language TEXT,\n"
                + "	host TEXT,\n"
                + "	upgrade_insecure_requests TEXT,\n"
                + "	connection TEXT,\n"
                + "	dnt TEXT,\n"
                + "	accept_encoding TEXT,\n"
                + "	user_agent TEXT,\n"
                + "	accept TEXT,\n"
                + "	content_length TEXT,\n"
                + "	pragma TEXT,\n"
                + "	referer TEXT,\n"
                + "	cache_control TEXT,\n"
                + "	cookie TEXT \n"
                + ");";
        
        try (Connection conn = DriverManager.getConnection(url); Statement stmt = conn.createStatement();) {
            // create a new table
            stmt.execute(sql);
            System.out.println("client table has been created.");
        } catch (SQLException e) {
            System.out.println("createClientTable SQLException "+e.getMessage());
        }
    }
	
	private Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:"+System.getProperty("user.dir") + File.separator + this.fileName;;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

}
