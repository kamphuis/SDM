/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phr;

import cpabe.*;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
/**
 *
 * @author luca
 */
public class Client {
    TrustedAuthorithy TA = new TrustedAuthorithy();
    private static String type;
    private static String pubk_location;
    private static String mk_location;
    private static String pvtk_location;
    private static String dec_location;
    private static String enc_location;
    
    // definition of the Client
    public Client(String type, String keys_location) throws Exception{
        if(type.equalsIgnoreCase("Patient") || 
                type.equalsIgnoreCase("Hospital") || 
                type.equalsIgnoreCase("Doctor") ||
                type.equalsIgnoreCase("Pharmacy") ||
                type.equalsIgnoreCase("Insurance") ||
                type.equalsIgnoreCase("Health Club") ||
                type.equalsIgnoreCase("Employer")) {
            this.type = type;
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
    
        ArrayList<String> result = new ArrayList<String>();
    
        try{
            result = s.executeSelect(table,fields);
            TA.keygen(pubk_location, pvtk_location, mk_location, table);
            for(String item : result) {
                TA.cpabe.dec(pubk_location, pvtk_location, item, dec_location);
            }
        }
        catch(Exception e) {}        
    }
    
    
    
    
}
