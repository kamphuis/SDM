
package phr;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.HashMap;
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
    private static final String db_url = "jdbc:mysql://kamphuis.student.utwente.nl:3306";
    private static final String db_name = "phr";
    private static final String db_driver = "com.mysql.jdbc.Driver";
    private static final String db_user = "phr_user";
    private static final String db_pw = "phr1234";
    Connection conn = null;
    LinkedHashMap<String, HashMap<String, String>> readAccessTree = 
            new LinkedHashMap<String, HashMap<String, String>>();
    LinkedHashMap<String, HashMap<String, String>> writeAccessTree = 
            new LinkedHashMap<String, HashMap<String, String>>();
    
    public Server(String type, String keys_location){
        this.pubk_location = keys_location + "pub_key";
        this.mk_location = keys_location + "m_key";
        this.pvtk_location = keys_location + "pvt_key";
        this.enc_location = keys_location + "enc_file";
        this.dec_location = keys_location + "dec_file";
        //addReadPolicy();
    }
    
    public void addReadPolicy(String table, String key, String policy){
        HashMap<String, String> h = new HashMap<String, String>();
        h.put(key, policy);
        readAccessTree.put(table, h);
    }
    
    public void addWritePolicy(String table, String key, String policy){
        HashMap<String, String> h = new HashMap<String, String>();
        h.put(key, policy);
        writeAccessTree.put(table, h);
    }
    
    public ArrayList<File> executeSelect(String table, List<String> fields) throws Exception{
        ArrayList <String> result = new ArrayList<String>();
        ArrayList <File> enc_result = new ArrayList<File>();
        String rows = "";
        for(String item : fields) {
            rows.concat(" " + item);
        }
        try {
            Class.forName(db_driver).newInstance();
            conn = DriverManager.getConnection(db_url+db_name,db_user,db_pw);
            Statement stat = conn.createStatement();
            ResultSet rs = stat.executeQuery("SELECT"+rows+"FROM "+table);
            for (int i=0; rs.next(); i++) {
                result.add( rs.getString(i) );
            }
            
            String POLICY = ""; //implement tree and policy generation
            
            for(int i = 0; i<result.size();i++){
                TA.cpabe.enc(pubk_location, POLICY, result.get(i), enc_location+i);
                enc_result.add(new File(enc_location+i));
            }
            
        }
        catch(Exception e){}
        
        conn.close();
        return enc_result;
    }
    
    public String getPubkLocation(){
        return this.pubk_location;
    }
    
    public String getMkLocation(){
        return this.mk_location;
    }
}
