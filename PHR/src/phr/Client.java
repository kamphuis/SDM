
package phr;


import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author luca
 */
public class Client {
    TrustedAuthorithy TA;
    private String attributes[] = new String[2];
    public String type;
    private String id;
    private String pubk_location;
    private String mk_location;
    private String pvtk_location;
    private String dec_location;
    private String enc_location;
    private String att_str = "";
    public String auth_location;

    // definition of the Client
    public Client(String type, String id, String keys_location, Server s, TrustedAuthorithy ta) throws Exception{
        if(type.equalsIgnoreCase(":Patient") || 
                type.equalsIgnoreCase(":Hospital") || 
                type.equalsIgnoreCase(":Doctor") ||
                type.equalsIgnoreCase(":Pharmacy") ||
                type.equalsIgnoreCase(":Insurance") ||
                type.equalsIgnoreCase(":Health-Club") ||
                type.equalsIgnoreCase(":Employer")) {
            this.TA=ta;
            this.type = type;
            this.id = id;
            this.pubk_location = s.getPubkLocation(id);
            this.mk_location = keys_location + "master_key";
            createFile(mk_location);
            this.pvtk_location = keys_location + "prv_key";
            createFile(pvtk_location);
            this.enc_location = keys_location + "enc_file.pdf.cpabe";
            createFile(enc_location);
            this.dec_location = keys_location + "dec_file.pdf.new";
            createFile(dec_location);
            this.auth_location = keys_location + "auth_file.pdf.new";
            createFile(auth_location);
            System.out.println("setup "+type+" "+id);
            this.attributes = TA.setup(pubk_location, mk_location, type, id);
            for(String item : attributes) {
                att_str = att_str.concat(item+" ");
            }
            att_str=att_str.substring(0, att_str.length()-1);
            System.out.println("generate keys "+att_str);
            TA.cpabe.keygen(pubk_location, pvtk_location, mk_location, att_str);
        }
        else {
            System.out.println("insert a valid type");
            Exception e = new Exception();
            throw e;
        }
    }
    
    // send a SQL request to the server
    public void read(String table, List<String> fields, String clause, Server s) throws Exception{
        
        try{
            String result_location = s.executeSelect(table,fields,id,clause);
            TA.cpabe.dec(pubk_location, pvtk_location, result_location, dec_location);
        }
        catch(Exception e) {}        
    }

    
    
    public void write(String table, List<String> fields, Server s) throws Exception{
        try{
            System.out.println(s.executeInsert(this,table,fields,id));
        }
        catch(Exception e) {}        
    }
    
    public String authWrite(String toAuth_location){
        try{
            TA.cpabe.dec(pubk_location, pvtk_location, toAuth_location, auth_location);
        }
        catch(Exception e){}
        
        return auth_location;
    }
    
    public static void createFile(String path) {
        try{
            File f = new File(path);
            f.createNewFile();
        }
        catch(IOException e){}
    }
    
}
