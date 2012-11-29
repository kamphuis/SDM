
package phr;

import java.io.*;
import java.sql.*;
import java.util.List;
import java.util.Scanner;
/**
 *
 * @author luca
 * 
 * The Server class has two main tasks:
 * 1) interact with the database
 * 2) store and manage the read and write access tree
 */
public class Server{
    TrustedAuthorithy TA;
    private String pubk_location;
    private String mk_location;
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
    
    /*
     * constructor, definition of the path to each file
     */ 
    public Server(String keys_location, TrustedAuthorithy ta){
        this.pubk_location = keys_location + "pub_key";
        this.mk_location = keys_location + "master_key";
        this.enc_location = keys_location + "enc_file.pdf.cpabe";
        this.input_location = keys_location + "input_file.pdf";
        createFile(input_location);
        this.read_tree_location = keys_location + "read_access_tree";
        this.write_tree_location = keys_location + "write_access_tree";
        this.TA = ta;
    }
    
    /* 
     * manage the definition of a new policy into the read access tree
     * policies are stored in a file as a triplet of lines as follow:
     * 1st line: table (where the policy is applied)
     * 2nd line: key (of the specific entry of the table)
     * 3rd line: policy
     * the combination between the table and the key gives the specific entry
     * where the policy applies
     */
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
    }
    
    /* 
     * manage the definition of a new policy into the write access tree
     * policies are stored in a file as a triplet of lines as follow:
     * 1st line: table (where the policy is applied)
     * 2nd line: key (of the specific entry of the table)
     * 3rd line: policy
     * the combination between the table and the key gives the specific entry
     * where the policy applies. 
     * Since updates have not been implemented this policies are never used 
     * after the first time that they have been defined (remember that policies 
     * are defined when something is written into the database by an entity)
     */
    public void addWritePolicy(String table, String key, String policy){
        if(getWritePolicy(table,key).equalsIgnoreCase("")) {
            try{
                FileWriter input_file = new FileWriter(write_tree_location,true);
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
    }
    
    /* 
     * retrieve the UNIQUE read policy with the specific table-key combination
     * by reading three lines at the time from the file where the read access 
     * tree is stored
     */
    public String getReadPolicy(String table, String key) {
        String result = "";
        try{
            FileReader fr_read = new FileReader(read_tree_location);
            Scanner scan = new Scanner(fr_read);
            boolean found = false;
            while((scan.hasNextLine())&&(!found)){
                String f1 = scan.nextLine();
                String f2 = scan.nextLine();
                String f3 = scan.nextLine(); 
                if((f1.equalsIgnoreCase(table))&&(f2.equalsIgnoreCase(key))){
                    result=f3;
                    found = true;
                }
            }    
            fr_read.close();
        }
        catch(Exception e){}
        return result;
    }
    
    /* 
     * retrieve the UNIQUE write policy with the specific table-key combination
     * by reading three lines at the time from the file where the write access 
     * tree is stored
     */
    public String getWritePolicy(String table, String key) {
        String result = "";
        try{
            FileReader fr_read = new FileReader(write_tree_location);
            Scanner scan = new Scanner(fr_read);
            boolean found = false;
            while((scan.hasNextLine())&&(!found)){
                String f1 = scan.nextLine();
                String f2 = scan.nextLine();
                String f3 = scan.nextLine();
                if((f1.equalsIgnoreCase(table))&&(f2.equalsIgnoreCase(key))){
                    result=f3;
                    found = true;
                }
            }    
            fr_read.close();
        }catch(Exception e){}
        return result;
    }
    
    /*
     * the server defines a select query using the paramenters that receives
     * from a client. the query is then passed to the DB and the result(s) is
     * encrypted by the server using: the public key that shares with the client
     * and the read policy for this specific entry of the table.
     * the location of the encrypted file is then passed to the client.
     * this location must be accessible to the client.
     */    
    public String executeSelect(Client client, String table, List<String> fields, String bsn, String clause) throws Exception{
        /* input file is used to store the result coming from the server and
         * then is passed to the enc algorithm to be encrypted.
         */
        FileWriter input_file = new FileWriter(input_location);
        BufferedWriter out;
        out = new BufferedWriter(input_file);
        
        // SELECT query generation
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
            
            // result from the query are written to the input file
            if(fields.get(0).equalsIgnoreCase("*")){
                while(rs.next()){
                    for(int i=1; i<=getSize(table); i++){
                    out.write(rs.getString(i));
                    out.newLine();
                    }
                }
            }
            else{
                while(rs.next()){
                    for(int i=1; i<=fields.size(); i++){
                        out.write(rs.getString(i));
                        out.newLine();
                    }
                }
            }
            
            out.close();
            
            System.out.print("the read policy of this table is: ");
            //retrieve the specific read policy
            String policy = getReadPolicy(table, bsn);
            System.out.println(policy);
            
            /* 
             * encrypt the file storing the query result(s) using the public key
             * shared with the client and the read policy of the specific entry
             * of the table
             */
            TA.cpabe.enc(pubk_location+"_"+client.id, policy, input_location, enc_location);
        }
        catch(Exception e){
            System.out.println("something went wrong executing SELECT");
            System.out.println(e.getMessage());
        }
      
        conn.close();
        
        // the location of the encrypted file is passed to the client
        return enc_location;
    }

    /*
     * this method manage the creation of new write and read policies when a 
     * client perform an INSERT operation on the database. 
     * policies are created for the table that is subjected to the SQL query
     * parameter id represents the key used to store the specific policy in the
     * read and write access tree
     * policies related to table that are subjected only to read operation 
     * (such as views) will be setup together with their correspondent tables
     */    
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
                        addReadPolicy("view_insurance", id, client.id+" :health-club 1of2");
                        addReadPolicy("view_insurance_LT", id, client.id+" true 1of2");
                    }
                    if(client.type.equalsIgnoreCase(":patient")) {
                        addReadPolicy(table, id, ":"+id+" "+fields.get(1)+" 1of2"); 
                    }
                    addReadPolicy("view_hospital", id,":hospital, :pharmacy");
                }
        
        else if(table.equalsIgnoreCase("medical_history")) {
                    if(client.type.equalsIgnoreCase(":hospital")|| client.type.equalsIgnoreCase(":doctor")) {
                        addReadPolicy(table, id, ":"+id+" :hospital :doctor 1of3");
                        addWritePolicy(table, id, ":hospital :doctor 1of2");
                    }
                }
        else if(table.equalsIgnoreCase("admittance")) {
                    if(client.type.equalsIgnoreCase(":hospital")|| client.type.equalsIgnoreCase(":doctor")) {
                        addReadPolicy(table, id, ":"+id+" :hospital :doctor 1of3");
                        addWritePolicy(table, id, ":hospital :doctor :health-club 1of3");
                        try {
                            Class.forName(db_driver).newInstance();
                            conn = DriverManager.getConnection(db_url+db_name,db_user,db_pw);
                            Statement stat = conn.createStatement();
                            String query = "SELECT employer FROM patient_data WHERE bsn='"+id+"'";
                            System.out.println("ecco la query "+query);
                            ResultSet rs = stat.executeQuery(query);
                            while(rs.next()){
                                addReadPolicy("view_employer", id,":"+rs.getString(1)+" true 1of2");
                            }
                        }
                        catch(Exception e){};
                    }
                }
        else if(table.equalsIgnoreCase("long-term_treatment")) {
                    if(client.type.equalsIgnoreCase(":hospital")|| client.type.equalsIgnoreCase(":doctor")) {
                        addReadPolicy(table, id, ":"+id+" :health-club :pharmacy 1of3");
                        addWritePolicy(table, id, ":hospital :doctor 1of2");
                    }
                }
    }
    
    /*
     * the server receive a write query (insert) from the client.
     * read and write policies are setup for the specific table/entry (if not 
     * present yet). 
     * Before submitted it to the DB the server needs to authenticate the client.
     * For doing so a challenge/response model has been defined as follow:
     * - the server generates a random number and write it into a file
     * - the file is encrypted using the public key that the server shares with 
     *   the client and the specific writing policy
     * - the location of the encrypted file is passed to the client.
     * - the client decrypts the file using its private key and (if succeed) it
     *   reads the content of the file
     * - the content is then send in plain to the server
     * - the server compare the received value with the originated random number
     * 
     * If the client is authenticate the insert operation is performed
     */    
    public String executeInsert(Client client, String table, List<String> fields, String bsn) {
        //setup the read and write policies (if not present yet)
        setupPolicies(client,table,fields,bsn);
        
        //generate a random number for challenge/response
        String random = ""+Math.random();
        boolean match = false;
        //CHALLENGE
        try {
            /*
             * the file that will be increpted is always the same file, server
             * does not keep track of clients queries
             */
            File input_file = new File(input_location);
            FileWriter in_file = new FileWriter(input_file.getAbsoluteFile());
            BufferedWriter out = new BufferedWriter(in_file);
            //random value written to a file
            out.write(random); out.close(); 
            try {
                //specific write policy is written from the access tree
                String policy = getWritePolicy(table, bsn);
                System.out.println("writing policy of the entry in the table: "+policy);
                /*file containing the random number is encrypted and passed to
                 * the client
                 */ 
                TA.cpabe.enc(pubk_location+"_"+client.id, policy, input_location, enc_location);
            }
            catch(Exception e){System.out.println(e.getMessage());}
            //RESPONSE
            String response_loc = client.authWrite(enc_location);
            FileReader fr = new FileReader(response_loc);
            BufferedReader br = new BufferedReader(fr);
            match = random.equals(br.readLine()); 
            br.close();
        }
	catch(IOException e) { 
            System.out.println("Something broke"); 
        }
        // if authenticate assemble the insert query and pass it to the DB
        if(match) {
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
        //otherwise notifies the failure
        else {
            return "Authentication failed!";
        }
    }
    
    //method to retrieve the server's public key location
    public String getPubkLocation(String name){
        
        return this.pubk_location+"_"+name;
    }
    
    //used to create the input file.
    public static void createFile(String path) {
        try{
            File f = new File(path);
            f.createNewFile();
        }
        catch(IOException e){}
    }
    
    //used for SQL-query results assemblation process
    public int getSize(String table){
        if(table.equalsIgnoreCase("patient_data")) return 11;
        else if(table.equalsIgnoreCase("admittance")) return 5;
        else if(table.equalsIgnoreCase("insurance")) return 7;
        else if(table.equalsIgnoreCase("long-term_treatment")) return 5;
        else if(table.equalsIgnoreCase("medical_history")) return 4;
        else if(table.equalsIgnoreCase("view_employer")) return 3;
        else if(table.equalsIgnoreCase("view_insurance")) return 5;
        else if(table.equalsIgnoreCase("view_insurance_LT")) return 4;
        else if(table.equalsIgnoreCase("view_hospital")) return 4;
        else return 1;
    }
}
