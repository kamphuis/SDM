
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
    TrustedAuthorithy TA = new TrustedAuthorithy();
    private LinkedList<String> attributes[] = (LinkedList<String>[]) new LinkedList[2];
    private String type;
    private String id;
    private String pubk_location;
    private String mk_location;
    private String pvtk_location;
    private String dec_location;
    private String enc_location;
    //private String input_location;
    
    // definition of the Client
    public Client(String type, String id, String keys_location) throws Exception{
        if(type.equalsIgnoreCase("Patient") || 
                type.equalsIgnoreCase("Hospital") || 
                type.equalsIgnoreCase("Doctor") ||
                type.equalsIgnoreCase("Pharmacy") ||
                type.equalsIgnoreCase("Insurance") ||
                type.equalsIgnoreCase("Health Club") ||
                type.equalsIgnoreCase("Employer")) {
            this.type = type;
            this.id = id;
            this.pubk_location = keys_location + "pub_key";
            this.mk_location = keys_location + "m_key";
            this.pvtk_location = keys_location + "pvt_key";
            this.enc_location = keys_location + "enc_file";
            this.dec_location = keys_location + "dec_file";
        }
        else {
            System.out.println("insert a valid type");
            Exception e = new Exception();
            throw e;
        }
    }
    
    // send a SQL request to the server
    public void read(String table, List<String> fields, Server s) throws Exception{
        
        try{
            this.attributes = TA.setup(s.getPubkLocation(), this.mk_location, this.type, this.id);
            String result_location = s.executeSelect(table,fields,id);
            String att_str = "";
            for(String item : attributes[1]) {
                att_str = att_str.concat(item+" ");
            }
            TA.keygen(pubk_location, pvtk_location, mk_location, att_str);
            
            TA.cpabe.dec(pubk_location, pvtk_location, result_location, dec_location);

        }
        catch(Exception e) {}        
    }

    
    
    public void write(String table, List<String> fields, List<String> values, Server s, String input_location) throws Exception{
        
          
        ArrayList<File> result = new ArrayList<File>();
    
        String policy = s.getWritePolicy(table, this.id);
      
        try{
            this.attributes = TA.setup(s.getPubkLocation(), this.mk_location, this.type, this.id);
            
            result = s.executeInsert(table,fields,id);
            
            //challenge-response
            
            
            
            TA.keygen(pubk_location, pvtk_location, mk_location, table);
            for(File item : result) {
                TA.cpabe.dec(pubk_location, pvtk_location, item.getPath(), dec_location);
            }
        }
        catch(Exception e) {}        
    }
    

}
