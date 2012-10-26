
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
    private String info;
    private String pubk_location;
    private String mk_location;
    private String pvtk_location;
    private String dec_location;
    private String enc_location;
    private String input_location;
    
    // definition of the Client
    public Client(String type, String info, String keys_location) throws Exception{
        if(type.equalsIgnoreCase("Patient") || 
                type.equalsIgnoreCase("Hospital") || 
                type.equalsIgnoreCase("Doctor") ||
                type.equalsIgnoreCase("Pharmacy") ||
                type.equalsIgnoreCase("Insurance") ||
                type.equalsIgnoreCase("Health Club") ||
                type.equalsIgnoreCase("Employer")) {
            this.type = type;
            this.info = info;
            this.pubk_location = keys_location + "pub_key";
            this.mk_location = keys_location + "m_key";
            this.pvtk_location = keys_location + "pvt_key";
            this.enc_location = keys_location + "enc_file";
            this.dec_location = keys_location + "dec_file";
            this.input_location = keys_location + "input_file";
        }
        else {
            System.out.println("insert a valid type");
            Exception e = new Exception();
            throw e;
        }
    }
    
    // send a SQL request to the server
    public void read(String table, List<String> fields, Server s) throws Exception{
        ArrayList<File> result = new ArrayList<>();
        try{
            this.attributes = TA.setup(s.getPubkLocation(), this.mk_location, this.type, this.info);
            result = s.executeSelect(table,fields,info);
            String att_str = "";
            for(String item : attributes[1]) {
                att_str = att_str.concat(item+" ");
            }
            TA.keygen(pubk_location, pvtk_location, mk_location, att_str);
            for(File item : result) {
                TA.cpabe.dec(pubk_location, pvtk_location, item.getPath(), dec_location);
            }
        }
        catch(Exception e) {}        
    }

    
    /* WORK IN PROGRESS
    public void write(String table, List<String> fields, List<String> values, Server s) throws Exception{
        
        File input = new File(input_location);
        
        ArrayList<File> result = new ArrayList<File>();
    
        String POLICY = ""; //implement tree, challenge/response and policy generation
        
        try{
            TA.setup(this.pubk_location, s.getMkLocation());
            TA.cpabe.enc(pubk_location, POLICY, input, enc_location);
            
            
            result = s.executeSelect(table,fields);
            TA.keygen(pubk_location, pvtk_location, mk_location, table);
            for(File item : result) {
                TA.cpabe.dec(pubk_location, pvtk_location, item.getPath(), dec_location);
            }
        }
        catch(Exception e) {}        
    }*/
    

}
