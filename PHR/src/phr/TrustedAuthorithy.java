
package phr;

import cpabe.*;
import java.io.IOException;
/**
 *
 * @author luca
 * 
 * the class TrustedAuthorithy works as a bridge to the CPABE library as it 
 * performs the call to all 4 algorithms implemented in the library.
 * Beside that, it manage the definition of the attributes for each new created
 * entity.
 */
public class TrustedAuthorithy {
    
    // instantiate the CPABE library
    Cpabe cpabe;
    
    public TrustedAuthorithy(){
        cpabe = new Cpabe();
    }
    
    /*
     * initialize the keys to the path specified by the location parameters. 
     * manage the definition of the read and write attributes to the entity
     * that initialize the setup procedure. 
     * a list of attributes of the setup entity are returned by this method
     */
    public String[] setup(String pubk_location, String mk_location, String type, String id) {
        
        String list[] = new String[2];
        try{
            //patients have as attributes their bsn
            if(type.equalsIgnoreCase(":patient")) {
                    list[0]=id; //write attributes
                    list[1]=id; //read attributes
                }
            
            //insurances have as attributes their id (e.g. menzis-enschede)
            else if(type.equalsIgnoreCase(":insurance")){
                    list[0]=id; //write attributes
                    list[1]=id; //read attributes
            }
            
            //employer have as attributes their id (e.g. post-nl)
            else if(type.equalsIgnoreCase(":employer")){
                    list[0]=id; //write attributes
                    list[1]=id; //read attributes
            }
            
            //other entities has as attributes their type (e.g. hospital, pharmacy ecc)
            else {
                list[0]=type; //write attributes
                list[1]=type; //read attributes
            }
            
            //initialize the public key and master key
            cpabe.setup(pubk_location, mk_location);
        }
        catch (IOException | ClassNotFoundException e) {System.out.println("duplicated id"); return null;}
        
        //read and write attributes returned in a list
        return list;
    }   
    
}
