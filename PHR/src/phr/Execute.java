/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phr;

import java.util.*;
/**
 *
 * @author luca
 */
public class Execute {
    
    public static void run() {
        TrustedAuthorithy TA = new TrustedAuthorithy();
        Server ser = new Server("/home/luca/NetBeansProjects/test/server/server_",TA);
        try{
            Client patient1 = new Client(":patient", ":45398289023", "/home/luca/NetBeansProjects/test/clients/patient1_", ser, TA);
            Client patient2 = new Client(":patient", ":45398289024", "/home/luca/NetBeansProjects/test/clients/patient2_", ser, TA);
            Client patient3 = new Client(":patient", ":45398289025", "/home/luca/NetBeansProjects/test/clients/patient3_", ser, TA);
            Client patient4 = new Client(":patient", ":45398289026", "/home/luca/NetBeansProjects/test/clients/patient4_", ser, TA);
            Client insurance = new Client(":insurance", ":Menzis", "/home/luca/NetBeansProjects/test/clients/menzis_", ser ,TA);
            Client hospital = new Client(":hospital", ":Ziekenhuis", "/home/luca/NetBeansProjects/test/clients/hospital_", ser ,TA);
            Client doctor = new Client(":doctor", ":dr.Gray", "/home/luca/NetBeansProjects/test/clients/doctor_", ser ,TA);
            Client employer = new Client(":employer", ":UT", "/home/luca/NetBeansProjects/test/clients/employer_", ser, TA);
            Client health_club = new Client(":health-club", ":HC1", "/home/luca/NetBeansProjects/test/clients/health_club_", ser, TA);
            
            List<String> l = new LinkedList<String>();
            l.add("bsn");l.add("name");l.add("surname");l.add("street");l.add("house_number");l.add("postcode");l.add("city");
            l.add("birth_date");l.add("gender");l.add("tel");l.add("employer");
            
            patient1.write("patient_data", l, ser);
            System.out.println("patient1 write");
            patient1.read("patient_data", l, "name=luca", ser);
            System.out.println("patient1 read");
            patient2.read("patient_data", l, "name=luca", ser);
            System.out.println("patient2 read");
            employer.read("patient_data", l, "name=luca", ser);
            System.out.println("employer read");
            
        }
        catch(Exception e){};
        
    }
    public static void main(String[] args){
        run();
    }
}
