/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phr;

import java.util.List;
import java.util.ArrayList;
import java.sql.*;
/**
 *
 * @author luca
 */
public class Server {
    TrustedAuthorithy TA = new TrustedAuthorithy();
    private static String pubk_location;
    private static String mk_location;
    private static String pvtk_location;
    private static String dec_location;
    private static String enc_location;
    private static final String db_url = "jdbc:mysql://kamphuis.student.utwente.nl:3306";
    private static final String db_name = "phr";
    private static final String db_driver = "com.mysql.jdbc.Driver";
    private static final String db_user = "phr_user";
    private static final String db_pw = "phr1234";
    Connection conn = null;
    
    public Server(String type, String keys_location){
        this.pubk_location = keys_location + "pub_key";
        this.mk_location = keys_location + "m_key";
        this.pvtk_location = keys_location + "pvt_key";
        this.enc_location = keys_location + "enc_file";
        this.dec_location = keys_location + "dec_file";
    }
    
    
    public ArrayList<String> executeSelect(String table, List<String> fields) throws Exception{
        ArrayList <String> result = new ArrayList<String>();
        String rows = "";
        //for(int i = 0; i<fields.size();i++) {
        for(String item : fields) {
            rows.concat(" " + fields.get(i));
        }
        try {
            Class.forName(db_driver).newInstance();
            conn = DriverManager.getConnection(db_url+db_name,db_user,db_pw);
            
            Statement stat = conn.createStatement();
            ResultSet rs = stat.executeQuery("SELECT"+rows+"FROM "+table);
        
            for (int i=0; rs.next(); i++) {
                result.add( rs.getString(i) );
            }
            
            for(String item : result){
                TA.cpabe
            }
            
            conn.close();
        }
        catch(Exception e){}
    }
    
}
