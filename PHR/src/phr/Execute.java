/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phr;

/**
 *
 * @author luca
 */
public class Execute {
    
    public static void main(String[] main) {
        TrustedAuthorithy TA = new TrustedAuthorithy();
        Server ser = new Server("/home/path-",TA);
    
        try{
            Client patient1 = new Client("patient", "45398289023", "path/location", ser, TA);
            Client patient2 = new Client("patient", "45398289024", "path/location", ser, TA);
            Client patient3 = new Client("patient", "45398289025", "path/location", ser, TA);
            Client patient4 = new Client("patient", "45398289026", "path/location", ser, TA);
            Client insurance = new Client("insurance", "Menzis", "path/location", ser ,TA);
            Client hospital = new Client("hospital", "Ziekenhuis", "path/location", ser ,TA);
            Client doctor = new Client("doctor", "dr.Gray", "path/location", ser ,TA);
            Client employer = new Client("employer", "UT", "path/location", ser, TA);
            Client health_club = new Client("health-club", "HC1", "path/location", ser, TA);
            
            
            
            
        }
        catch(Exception e){};
        
    }
}
