
package phr;

import cpabe.*;
import java.io.IOException;
/**
 *
 * @author luca
 */
public class TrustedAuthorithy {
    
    Cpabe cpabe;
    
    
    public TrustedAuthorithy(){
        cpabe = new Cpabe();
    }
    
    
    public void setup(String pubk_location, String mk_location) throws IOException {
        try{
            cpabe.setup(pubk_location, mk_location);
        }
        catch (Exception e) {}
    }   
    
    public void keygen(String pubk_location, String prvk_location, String mk_location, String attr_str) throws IOException {
        try{
            cpabe.keygen(pubk_location, prvk_location, mk_location, attr_str);
        }
        catch (Exception e) {}
    }
    
}
