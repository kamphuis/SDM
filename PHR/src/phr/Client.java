
package phr;


import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author luca
 */
public class Client {
    TrustedAuthorithy TA;
    private LinkedList<String> attributes[] = (LinkedList<String>[]) new LinkedList[2];
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
        if(type.equalsIgnoreCase("Patient") || 
                type.equalsIgnoreCase("Hospital") || 
                type.equalsIgnoreCase("Doctor") ||
                type.equalsIgnoreCase("Pharmacy") ||
                type.equalsIgnoreCase("Insurance") ||
                type.equalsIgnoreCase("Health Club") ||
                type.equalsIgnoreCase("Employer")) {
            this.TA=ta;
            this.type = type;
            this.id = id;
            this.pubk_location = keys_location + "pub_key";
            this.mk_location = keys_location + "m_key";
            this.pvtk_location = keys_location + "pvt_key";
            this.enc_location = keys_location + "enc_file";
            this.dec_location = keys_location + "dec_file";
            this.auth_location = keys_location + "auth_file";
            this.attributes = TA.setup(s.getPubkLocation(), this.mk_location, this.type, this.id);
            for(String item : attributes[0]) {
                att_str = att_str.concat(item+" ");
            }
            for(String item : attributes[1]) {
                att_str = att_str.concat(item+" ");
            }
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

    
    
    public void write(String table, List<String> fields, Server s, String input_location) throws Exception{
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
    
    
}
