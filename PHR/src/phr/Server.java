
package phr;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
/**
 *
 * @author luca
 */
public class Server {
    TrustedAuthorithy TA = new TrustedAuthorithy();
    private String pubk_location;
    private String mk_location;
    private String pvtk_location;
    private String dec_location;
    private String enc_location;
    private String input_location;
    private static final String db_url = "jdbc:mysql://kamphuis.student.utwente.nl:3306";
    private static final String db_name = "phr";
    private static final String db_driver = "com.mysql.jdbc.Driver";
    private static final String db_user = "phr_user";
    private static final String db_pw = "phr1234";
    Connection conn = null;
    LinkedHashMap<String, HashMap<String, String>> readAccessTree = 
            new LinkedHashMap<>();
    LinkedHashMap<String, HashMap<String, String>> writeAccessTree = 
            new LinkedHashMap<>();
    
    public Server(String type, String keys_location){
        this.pubk_location = keys_location + "pub_key";
        this.mk_location = keys_location + "m_key";
        this.pvtk_location = keys_location + "pvt_key";
        this.enc_location = keys_location + "enc_file";
        this.dec_location = keys_location + "dec_file";
        this.input_location = keys_location + "input_file";
    }
    
    public void addReadPolicy(String table, String key, String policy){
        HashMap<String, String> h = new HashMap<>();
        h.put(key, policy);
        readAccessTree.put(table, h);
    }
    
    public void addWritePolicy(String table, String key, String policy){
        HashMap<String, String> h = new HashMap<>();
        h.put(key, policy);
        writeAccessTree.put(table, h);
    }
    
    public String getReadPolicy(String table, String key) {
        return readAccessTree.get(table).get(key);
    }
    
    public String getWritePolicy(String table, String key) {
        return writeAccessTree.get(table).get(key);
    }
    
    public String executeSelect(String table, List<String> fields, String id, String clause) throws Exception{
        ArrayList<String> result = new ArrayList<>();
        FileWriter input_file = new FileWriter(input_location);
        BufferedWriter out = new BufferedWriter(input_file);
        String rows = "";
        for(String item : fields) {
            rows = rows.concat(" " + item);
        }
        try {
            Class.forName(db_driver).newInstance();
            conn = DriverManager.getConnection(db_url+db_name,db_user,db_pw);
            Statement stat = conn.createStatement();
			String query = "SELECT "+rows+" FROM "+table;
			if(!clause.equals("")) { query = query+" WHERE "+clause; }
            ResultSet rs = stat.executeQuery(query);
            for (int i=0; rs.next(); i++) {
                result.add( rs.getString(i) );
            }
            
            for(int i = 0; i<result.size();i++){
                out.write(result.get(i));
                out.newLine();
            }
            
            String policy = getReadPolicy(table, id);
            
            TA.cpabe.enc(pubk_location, policy, input_location, enc_location);
            
        }
        catch(Exception e){}
        
        conn.close();
        return enc_location;
    }
	
	public String executeInsert(Client client, String table, List<String> fields, String id) {
		String policy = getWritePolicy(table, id);
		String random = ""+Math.random();
		FileWriter input_file = new FileWriter(input_location);
        BufferedWriter out = new BufferedWriter(input_file);
		try { out.write(random); out.newLine(); out.close(); }
		catch(IOException e) { System.out.println("Something broke"); }
		TA.cpabe.enc(pubk_location, policy, input_location, enc_location);
		String response_loc = client.authWrite(enc_location);
		
		FileReader fr = new FileReader(response_loc);
		BufferedReader br = new BufferedReader(fr);
		boolean match = false;
		try { match = random.equals(br.readLine()); br.close();}
		catch(IOException e) { System.out.println("Something broke"); }
		if(match) { //dostuff 
			String values = "(";
			for(String item : fields) {
				values = values.concat("'"+item+"', ");
			}
			values = values.substring(0,values.length()-3);
			values = values + ")";
			String returnstring = "";
			try {
				Class.forName(db_driver).newInstance();
				conn = DriverManager.getConnection(db_url+db_name,db_user,db_pw);
				Statement stat = conn.createStatement();
				String query = "INSERT INTO "+table+" VALUES "+values;
				int num_rows_affected = stat.executeUpdate(query);
				conn.close();
				returnstring = "Insert statement executed. Number of rows affected = "+num_rows_affected;
            }
			catch(Exception e) {
				returnstring = "Something went wrong executing insert statement";
			}
			return returnstring;
		}
		else { 
			return "Authentication failed!";
		}
	}
    
    public String getPubkLocation(){
        return this.pubk_location;
    }
    
    public String getMkLocation(){
        return this.mk_location;
    }
}
