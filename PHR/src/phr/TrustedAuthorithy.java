
package phr;

import cpabe.*;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
/**
 *
 * @author luca
 */
public class TrustedAuthorithy {
    
    Cpabe cpabe;
    List<String> bsn = new LinkedList<String>();
    List<String> insurances = new LinkedList<String>();
    
    public TrustedAuthorithy(){
        cpabe = new Cpabe();
    }
    
    //id is the bsn in case of a client or the company name in case of an insurance company
    //in case of any other entity the info is useless and can be left empty
    public String[] setup(String pubk_location, String mk_location, String type, String id) {
        
        String list[] = new String[2];
        try{
            if(type.equalsIgnoreCase(":patient")) {
                if(!bsn.contains(id)){
                    bsn.add(id);
                    list[0]=type; //write attributes
                    list[1]=id; //read attributes
                }
                else throw new IOException();
                    
            }
            else if(type.equalsIgnoreCase(":insurance")){
                if(!insurances.contains(id)){
                    insurances.add(id);
                    list[0]=type; //write attributes
                    list[1]=id; //read attributes
                }
                else throw new IOException();
            }
            else {
                list[0]=type; //write attributes
                list[1]=type; //read attributes
            }
            cpabe.setup(pubk_location, mk_location);
        }
        catch (IOException | ClassNotFoundException e) {System.out.println("duplicated id"); return null;}
        return list;
    }   
    
}
