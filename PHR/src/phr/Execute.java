/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phr;

import java.util.*;
import java.io.*;
/**
 *
 * @author luca
 */
public class Execute {
    
    public static void run() {
        String newLine = System.getProperty("line.separator");
        InputStreamReader istream = new InputStreamReader(System.in) ;
        BufferedReader bufRead = new BufferedReader(istream) ;
        TrustedAuthorithy TA = new TrustedAuthorithy();
        Server ser = new Server("/home/luca/NetBeansProjects/test/server/server_",TA);
        try{
            boolean choice = false;
            while(!choice){
                System.out.println(newLine);
                System.out.println("press 1 to insert general personal data as a patient");
                System.out.println("press 2 to read data as a doctor");
                System.out.println("press 3 to read data as the insurance");
                System.out.println("press 4 to read data as the employer");
                System.out.println("press 5 to insert health data as a hospital");
                System.out.println("press 6 to insert health data as a health-club");
                System.out.println("press 7 to exit");
                int sel = Integer.parseInt(bufRead.readLine());
                String selection = "";
                if(sel==1){
                    Client patient1 = new Client(":patient", ":458289023", "/home/luca/NetBeansProjects/test/clients/patient", ser, TA);
                    List<String> l = new LinkedList<String>();
                    System.out.print("insert your BSN (9 characters): ");
                    l.add(bufRead.readLine());
                    System.out.print("insert your name (max 30 characters): ");
                    l.add("'"+bufRead.readLine()+"'");
                    System.out.print("insert your surname (max 30 characters): ");
                    l.add("'"+bufRead.readLine()+"'");
                    System.out.print("insert your street (max 20 characters): ");
                    l.add("'"+bufRead.readLine()+"'");
                    System.out.print("insert your house number (max 3 digits): ");
                    l.add(bufRead.readLine());
                    System.out.print("insert your postcode (1111AA): ");
                    l.add("'"+bufRead.readLine()+"'");
                    System.out.print("insert your city (max 20 character): ");
                    l.add("'"+bufRead.readLine()+"'");
                    System.out.print("insert your birth date (YYYY-MM-DD): ");
                    l.add("'"+bufRead.readLine()+"'");
                    System.out.print("insert your gender (male/female): ");
                    l.add("'"+bufRead.readLine()+"'");
                    System.out.print("insert your telephone (max 11 digits): ");
                    l.add(bufRead.readLine());
                    System.out.print("insert the name of your employer (max 50 characters): ");
                    l.add("'"+bufRead.readLine()+"'");
                    System.out.println("");
                    selection = "";
                    while(!selection.equalsIgnoreCase("w")){
                        System.out.println("");
                        System.out.println("press w to confirm the writing");
                        selection = bufRead.readLine();
                    }
                    patient1.write("patient_data", l, ser);
                }
                else if(sel==2) {
                        List<String> l1 = new LinkedList<String>();
                        Client doctor = new Client(":doctor", ":dr.Gray", "/home/luca/NetBeansProjects/test/clients/doctor", ser ,TA);
                        System.out.println("what table you would like to read: ");
                        String table = bufRead.readLine();
                        System.out.println("what fields you would like to read from the table (inser q to terminate)");
                        while(!selection.equalsIgnoreCase("q")){
                            selection = bufRead.readLine();
                            l1.add(selection);
                        }
                        l1.remove("q");
                        System.out.println("what filter you want to apply to your research");
                        String clause = bufRead.readLine();
                        selection = "";
                        while(!selection.equalsIgnoreCase("r")){
                            System.out.println("");
                            System.out.println("press r to confirm the reading");
                            selection = bufRead.readLine();
                        }
                        doctor.read(table, l1, clause , ser);
                    }
                else if(sel==3) {
                        List<String> l1 = new LinkedList<String>();
                        Client insurance = new Client(":insurance", ":Menzis", "/home/luca/NetBeansProjects/test/clients/insurance", ser ,TA);
                        System.out.println("what table you would like to read: ");
                        String table = bufRead.readLine();
                        System.out.println("what fields you would like to read from the table (inser q to terminate)");
                        while(!selection.equalsIgnoreCase("q")){
                            selection = bufRead.readLine();
                            l1.add(selection);
                        }
                        l1.remove("q");
                        System.out.println("what filter you want to apply to your research");
                        String clause = bufRead.readLine();
                        selection = "";
                        while(!selection.equalsIgnoreCase("r")){
                            System.out.println("");
                            System.out.println("press r to confirm the reading");
                            selection = bufRead.readLine();
                        }
                        insurance.read(table, l1, clause , ser);
                 }
                else if(sel==4) {
                        List<String> l1 = new LinkedList<String>();
                        Client employer = new Client(":employer", ":UT", "/home/luca/NetBeansProjects/test/clients/employer", ser, TA);
                        System.out.println("what table you would like to read: ");
                        String table = bufRead.readLine();
                        System.out.println("what fields you would like to read from the table (inser q to terminate)");
                        while(!selection.equalsIgnoreCase("q")){
                            selection = bufRead.readLine();
                            l1.add(selection);
                        }
                        l1.remove("q");
                        System.out.println("what filter you want to apply to your research");
                        String clause = bufRead.readLine();
                        selection = "";
                        while(!selection.equalsIgnoreCase("r")){
                            System.out.println("");
                            System.out.println("press r to confirm the reading");
                            selection = bufRead.readLine();
                        }
                        employer.read(table, l1, clause , ser);
                }
                else if(sel==5) {
                    Client hospital = new Client(":hospital", ":Ziekenhuis", "/home/luca/NetBeansProjects/test/clients/hospital", ser ,TA);
                    selection = "";
                    List<String> l01 = new LinkedList<String>();
                    List<String> l02 = new LinkedList<String>();
                    System.out.print("insert patient BSN (9 characters): ");
                    l01.add(bufRead.readLine());
                    System.out.print("insert hospital's name (max 50 characters): ");
                    l01.add("'"+bufRead.readLine()+"'");
                    System.out.print("insert the treatment id (max 10 characters): ");
                    String t_id = "'"+bufRead.readLine()+"'";
                    l01.add(t_id);
                    l02.add(t_id);
                    System.out.print("insert the entry date (YYYY-MM-DD): ");
                    l01.add("'"+bufRead.readLine()+"'");
                    System.out.print("insert the exit date (YYYY-MM-DD): ");
                    l01.add(bufRead.readLine());
                    System.out.print("insert the id of the responsible doctor (max 10 characters): ");
                    l02.add("'"+bufRead.readLine()+"'");
                    System.out.print("insert a brief description of the treatment (max 100 characters): ");
                    l02.add("'"+bufRead.readLine()+"'");
                    System.out.print("insert the administered drugs (max 100 characters): ");
                    l02.add("'"+bufRead.readLine()+"'");
                    System.out.println("");
                    selection = "";
                    while(!selection.equalsIgnoreCase("w")){
                        System.out.println("");
                        System.out.println("press w to confirm the writing");
                        selection = bufRead.readLine();
                    }
                    hospital.write("admittance", l01, ser);
                    hospital.write("medical_history", l01, ser);
                }
                else if(sel==6) {
                    Client health_club = new Client(":health-club", ":HC1", "/home/luca/NetBeansProjects/test/clients/health_club", ser, TA);
                }
                else choice=true;
            
            }
        }
        catch(Exception e){};    
    }
    
    public static void main(String[] args){
            run();
    }
}
