
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
    TrustedAuthorithy TA;
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
    
    public Server(String keys_location, TrustedAuthorithy ta){
        this.pubk_location = keys_location + "pub_key";
        createFile(pubk_location);
        this.mk_location = keys_location + "master_key";
        createFile(mk_location);
        this.pvtk_location = keys_location + "prv_key";
        createFile(pvtk_location);
        this.enc_location = keys_location + "enc_file.pdf.cpabe";
        createFile(enc_location);
        this.dec_location = keys_location + "dec_file.pdf.new";
        createFile(dec_location);
        this.input_location = keys_location + "input_file.pdf";
        createFile(input_location);
        this.TA = ta;
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
        BufferedWriter out;
        out = new BufferedWriter(input_file);
        String rows = "";
        for(String item : fields) {
            rows = rows.concat(" " + item);
        }
        try {
            Class.forName(db_driver).newInstance();
            conn = DriverManager.getConnection(db_url+db_name,db_user,db_pw);
            Statement stat = conn.createStatement();
			String query = "SELECT"+rows+" FROM "+table;
			if(!clause.equals("")) { query = query+" WHERE "+clause; }
            ResultSet rs = stat.executeQuery(query);
            for (int i=0; rs.next(); i++) {
                result.add( rs.getString(i) );
            }
            
            for(int i = 0; i<result.size();i++){
                out.write(result.get(i));
                out.newLine();
            }
            
            out.close();
            String policy = getReadPolicy(table, id);
            TA.cpabe.enc(pubk_location, policy, input_location, enc_location);
            
        }
        catch(Exception e){System.out.println("something went wrong executing SELECT");}
      
        conn.close();
        return enc_location;
    }

    public void setupPolicies(Client client, String table, List<String> fields, String id) {
        switch(table) {
                case "patient_data": {
                    if(client.type.equalsIgnoreCase(":patient")) {
                        addReadPolicy(table, id, id+" hospital doctor 1of3");
                        addReadPolicy("view-employer", id, fields.get(10)+" true 1of2");
                        addWritePolicy(table, id, id+" true 1of2");
                    }
                }
                case "insurance": {
                    if(client.type.equalsIgnoreCase(":insurance")) {
                        addReadPolicy(table, id, id+" "+fields.get(0)+" 1of2"); 
                        addReadPolicy("view-insurance", id, id+" health-club 1of2");
                        addReadPolicy("view-insurance-LT", id, id+" true 1of2");
                        addWritePolicy(table, id, id+" true 1of2");
                    }
                    if(client.type.equalsIgnoreCase(":patient")) {
                        addReadPolicy(table, id, id+" "+fields.get(1)+" true 1of2"); 
                    }
                }
                case "medical_history": {
                    if(client.type.equalsIgnoreCase(":hospital")|| client.type.equalsIgnoreCase(":doctor")) {
                        addReadPolicy(table, id, fields.get(0)+" hospital doctor 1of3"); //ISSUE!
                        addReadPolicy("view-hospital", id, "hospital pharmacy 1of2");
                        addWritePolicy(table, id, "hospital doctor 1of2");
                    }
                }
                case "admittance": {
                    if(client.type.equalsIgnoreCase(":hospital")|| client.type.equalsIgnoreCase(":doctor")) {
                        addReadPolicy(table, id, fields.get(0)+" hospital doctor 1of3");
                        addWritePolicy(table, id, "hospital doctor healtclub 1of3");
                    }
                }
                case "long-term_treatment": {
                    if(client.type.equalsIgnoreCase(":hospital")|| client.type.equalsIgnoreCase(":doctor")) {
                        addReadPolicy(table, id, fields.get(0)+" health-club pharmacy 1of3");
                        addWritePolicy(table, id, "hospital doctor 1of2");
                    }
                }
                
        }
    }
    
    public String executeInsert(Client client, String table, List<String> fields, String id) {
        setupPolicies(client,table,fields,id);
        String policy = getWritePolicy(table, id);
        String random = ""+Math.random();
        boolean match = false;
        try {
            File input_file = new File(input_location);
            FileWriter in_file = new FileWriter(input_file.getAbsoluteFile());
            BufferedWriter out = new BufferedWriter(in_file);
            out.write(random); out.close(); 
            try {
                TA.cpabe.enc(pubk_location, policy, input_location, enc_location);
            }
            catch(Exception e){}
            String response_loc = client.authWrite(enc_location);
            FileReader fr = new FileReader(input_location);
            BufferedReader br = new BufferedReader(fr);
            match = random.equals(br.readLine()); 
            br.close();
        }
	catch(IOException e) { 
            System.out.println("Something broke"); 
        }
        if(match) {
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
    
    
    public String getPubkLocation(String name){
        
        return this.pubk_location+"_"+name;
    }
    
    public String getMkLocation(){
        return this.mk_location;
    }
    
    public static void createFile(String path) {
        try{
            File f = new File(path);
            f.createNewFile();
        }
        catch(IOException e){}
    }
}
