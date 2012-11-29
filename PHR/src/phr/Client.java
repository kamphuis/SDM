
package phr;


import java.util.List;

/**
 *
 * @author luca
 * 
 * Class Client
 * 
 */
public class Client {
    TrustedAuthorithy TA;
    private String attributes[] = new String[2];
    public String type;
    public String id;
    private String pubk_location;
    private String mk_location;
    private String pvtk_location;
    private String dec_location;
    private String enc_location;
    public String att_str = "";
    public String auth_location;
    
    /* 
     * definition of the Client
     * the client invoke the setup operation at the TA which will distribute the
     * public key to the server and the master key to the client.
     * together with that the TA defines the attributes of the client which will
     * be use in the key generation by the TA of the client's master key
     */
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
            keys_location = keys_location+"_"+id+"_";
            this.pubk_location = s.getPubkLocation(id);
            this.mk_location = keys_location + "master_key";
            this.pvtk_location = keys_location + "prv_key";
            this.enc_location = keys_location + "enc_file.pdf.cpabe";
            this.dec_location = keys_location + "dec_file.pdf.new";
            this.auth_location = keys_location + "auth_file.pdf.new";
            System.out.println("setup "+type+" "+id);
            
            /*
             * setup the entity will return the attributes for this entity.
             * if the entity already exist the public and master key will
             * be overwritten (they will be exactly the same).
             */
            this.attributes = TA.setup(pubk_location, mk_location, type, id);
            for(String item : attributes) {
                att_str = att_str.concat(item+" ");
            }
            att_str=att_str.substring(0, att_str.length()-1);
            System.out.println("with attributes ==> "+att_str);
            System.out.println("generate keys related to attributes ");
            
            //generate private key related to attributes
            TA.cpabe.keygen(pubk_location, pvtk_location, mk_location, att_str);
        }
        else {
            System.out.println("insert a valid type");
            Exception e = new Exception();
            throw e;
        }
    }
    
    /*
     * read a specific entry(s) in the table. call executeSelect of Server class.
     * table specifies which table(s) we desired to read
     * fields specifies which fields of the table(s) (* is also accepted)
     * clause specifies a clause for the SELECT query
     * bsn is the key for access the policies inside read access trees
     * executeSelect will return the location of the encrypted file containing
     * the result(s) of the select query and the user will decrypt the file 
     * using its private key and the public key that shares with the server
     */ 
    public void read(String table, List<String> fields, String clause, Server s, String bsn) throws Exception{
        
        try{
            String result_location = s.executeSelect(this,table,fields,bsn,clause);
            TA.cpabe.dec(pubk_location, pvtk_location, result_location, dec_location);
        }
        catch(Exception e) {}        
    }

    
    /*
     * write a specific entry(s) in the table. call executeInsert of Server class.
     * table specifies which table(s) we desired to write
     * fields specifies which fields of the table(s)
     * bsn is the key for access the policies inside write access trees
     * executeInsert will return if the insert query was successful or not
     */ 
    public void write(String table, List<String> fields, Server s, String bsn) throws Exception{
        try{
            System.out.println(s.executeInsert(this,table,fields,bsn));
        }
        catch(Exception e) {}        
    }
    
    /*
     * used to decrypt the challenge received from the server (stored in 
     * toAuth_location which needs to be accessible to the client)
     * the path (auth_location) of the resulting plaintext is returned.
     * the file needs to be accessible to the server.
     */ 
    public String authWrite(String toAuth_location){
        try{
            TA.cpabe.dec(pubk_location, pvtk_location, toAuth_location, auth_location);
        }
        catch(Exception e){}
        
        return auth_location;
    }
}
