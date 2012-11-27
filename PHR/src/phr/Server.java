
package phr;

import java.io.*;
import java.sql.*;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
/**
 *
 * @author luca
 */
public class Server implements Serializable{
    TrustedAuthorithy TA;
    private String pubk_location;
    private String mk_location;
    private String pvtk_location;
    private String dec_location;
    private String enc_location;
    private String input_location;
    private String read_tree_location;
    private String write_tree_location;
    private static final String db_url = "jdbc:mysql://localhost:3306/";
    private static final String db_name = "phr";
    private static final String db_driver = "com.mysql.jdbc.Driver";
    private static final String db_user = "root";
    private static final String db_pw = "valtuz";
    Connection conn = null;
    
    public Server(String keys_location, TrustedAuthorithy ta){
        this.pubk_location = keys_location + "pub_key";
        this.mk_location = keys_location + "master_key";
        this.pvtk_location = keys_location + "prv_key";
        this.enc_location = keys_location + "enc_file.pdf.cpabe";
        this.dec_location = keys_location + "dec_file.pdf.new";
        this.input_location = keys_location + "input_file.pdf";
        createFile(input_location);
        this.read_tree_location = keys_location + "read_access_tree";
        this.write_tree_location = keys_location + "write_access_tree";
        this.TA = ta;
    }
    
    public void addReadPolicy(String table, String key, String policy){
        if(getReadPolicy(table,key).equalsIgnoreCase("")) {
            try{
                FileWriter input_file = new FileWriter(read_tree_location,true);
                BufferedWriter out;
                out = new BufferedWriter(input_file);
                out.write(table);
                out.newLine();
                out.write(key);
                out.newLine();
                out.write(policy);
                out.newLine();
                out.close();
            } catch(Exception e){};
        }
        else return;
    }
    
    public void addWritePolicy(String table, String key, String policy){
        if(getWritePolicy(table,key).equalsIgnoreCase("")) {
            try{
                FileWriter input_file = new FileWriter(write_tree_location,true);
                BufferedWriter out = new BufferedWriter(input_file);
                out.write(table);
                out.newLine();
                out.write(key);
                out.newLine();
                out.write(policy);
                out.newLine();
                out.close();
            } catch(Exception e){};
        }
        else return;
    }
    
    public String getReadPolicy(String table, String key) {
        String result = "";
        try{
            FileReader fr_read = new FileReader(read_tree_location);
            Scanner scan1 = new Scanner(fr_read);
            boolean found = false;
            while((scan1.hasNextLine())&&(!found)){
                String f1 = scan1.nextLine();
                String f2 = scan1.nextLine();
                if((f1.equalsIgnoreCase(table))&&(f2.equalsIgnoreCase(key))){
                    result=scan1.nextLine();
                    found = true;
                }
                else scan1.nextLine();
            }    
            fr_read.close();
        }
        catch(Exception e){}
        return result;
    }
    
    public String getWritePolicy(String table, String key) {
        String result = "";
        try{
            FileReader fr_read = new FileReader(write_tree_location);
            Scanner scan = new Scanner(fr_read);
            boolean found = false;
            while((scan.hasNextLine())&&(!found)){
                String f1 = scan.nextLine();
                String f2 = scan.nextLine();
                if((f1.equalsIgnoreCase(table))&&(f2.equalsIgnoreCase(key))){
                    result=scan.nextLine();
                    found = true;
                }
                else scan.nextLine();
            }    
            fr_read.close();
        }catch(Exception e){}
        return result;
    }
    
    public String executeSelect(Client client, String table, List<String> fields, String bsn, String clause) throws Exception{
        FileWriter input_file = new FileWriter(input_location);
        BufferedWriter out;
        out = new BufferedWriter(input_file);
        String rows = "";
        for(String item : fields) {
            rows = rows.concat(item+", ");
        }
        rows=rows.substring(0, rows.length()-2);
        try {
            Class.forName(db_driver).newInstance();
            conn = DriverManager.getConnection(db_url+db_name,db_user,db_pw);
            Statement stat = conn.createStatement();
            String query = "SELECT "+rows+" FROM "+table;
            if(!clause.equals("")) { 
                query = query+" WHERE "+clause; 
            }
            System.out.println(query);
            ResultSet rs = stat.executeQuery(query);
            
            while(rs.next()){
                for(int i=1; i<=fields.size(); i++){
                    out.write(rs.getString(i));
                    out.newLine();
                }
            }
            
            out.close();
            System.out.print("the read policy of this table is: ");
            System.out.println(getReadPolicy(table, bsn));
            String policy = getReadPolicy(table, bsn);
            TA.cpabe.enc(pubk_location+"_"+client.id, policy, input_location, enc_location);
        }
        catch(Exception e){
            System.out.println("something went wrong executing SELECT");
            System.out.println(e.getMessage());
        }
      
        conn.close();
        return enc_location;
    }

    public void setupPolicies(Client client, String table, List<String> fields, String id) {
        if(table.equalsIgnoreCase("patient_data")) {
                    if(client.type.equalsIgnoreCase(":patient")) {
                        addReadPolicy(table, id, ":"+id+" :hospital :doctor 1of3");
                        addWritePolicy(table, id, ":"+id+" true 1of2");
                    }
                }
        
        else if(table.equalsIgnoreCase("insurance")) {
                    if(client.type.equalsIgnoreCase(":insurance")) {
                        addReadPolicy(table, id, ":"+id+" "+client.id+" 1of2"); 
                        addWritePolicy(table, id, client.id+" true 1of2");
                    }
                    if(client.type.equalsIgnoreCase(":patient")) {
                        addReadPolicy(table, id, ":"+id+" "+fields.get(1)+" 1of2"); 
                    }
                }
        else if(table.equalsIgnoreCase("medical_history")) {
                    if(client.type.equalsIgnoreCase(":hospital")|| client.type.equalsIgnoreCase(":doctor")) {
                        addReadPolicy(table, id, ":"+id+" :hospital :doctor 1of3"); //ISSUE
                        addWritePolicy(table, id, ":hospital :doctor 1of2");
                    }
                }
        else if(table.equalsIgnoreCase("admittance")) {
                    if(client.type.equalsIgnoreCase(":hospital")|| client.type.equalsIgnoreCase(":doctor")) {
                        addReadPolicy(table, id, ":"+id+" :hospital :doctor 1of3");
                        addWritePolicy(table, id, ":hospital :doctor :healtclub 1of3");
                    }
                }
        else if(table.equalsIgnoreCase("long-term_treatment")) {
                    if(client.type.equalsIgnoreCase(":hospital")|| client.type.equalsIgnoreCase(":doctor")) {
                        addReadPolicy(table, id, ":"+id+" :health-club :pharmacy 1of3");
                        addWritePolicy(table, id, ":hospital :doctor 1of2");
                    }
                }
    }
    
    public String executeInsert(Client client, String table, List<String> fields, String bsn) {
        setupPolicies(client,table,fields,bsn);
        String random = ""+Math.random();
        boolean match = false;
        try {
            File input_file = new File(input_location);
            FileWriter in_file = new FileWriter(input_file.getAbsoluteFile());
            BufferedWriter out = new BufferedWriter(in_file);
            out.write(random); out.close(); 
            try {
                String policy = getWritePolicy(table, bsn);
                System.out.println("writing policy of the entry in the table: "+policy);
                TA.cpabe.enc(pubk_location+"_"+client.id, policy, input_location, enc_location);
            }
            catch(Exception e){System.out.println(e.getMessage());}
            String response_loc = client.authWrite(enc_location);
            FileReader fr = new FileReader(response_loc);
            BufferedReader br = new BufferedReader(fr);
            match = random.equals(br.readLine()); 
            br.close();
        }
	catch(IOException e) { 
            System.out.println("Something broke"); 
        }
        if(match) {
            System.out.println(client.type+" start writing");
            String values = "(";
            for(String item : fields) {
		values = values.concat(item+", ");
            }
            values = values.substring(0,values.length()-2);
            values = values + ")";
            String returnstring = "";
            try {
                Class.forName(db_driver).newInstance();
		conn = DriverManager.getConnection(db_url+db_name,db_user,db_pw);
		Statement stat = conn.createStatement();
		String query = "INSERT INTO "+table+" VALUES "+values;
                System.out.println(query);
                int num_rows_affected = stat.executeUpdate(query);
		conn.close();
		returnstring = "Insert statement executed. Number of rows affected = "+num_rows_affected;
            }
            catch(Exception e) {
                System.out.println(e.getMessage());
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
