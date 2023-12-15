package Helper;

import Agents.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import jade.core.AID;
import jade.core.Agent;
import jade.core.ContainerID;
import jade.core.ProfileImpl;
import jade.domain.JADEAgentManagement.CreateAgent;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import jade.core.Runtime;



public class CfgReader {

    public static String CarsContainerName;
    public static String OrdersContainerName; 

    public static jade.wrapper.AgentContainer carsContainer;
    public static jade.wrapper.AgentContainer ordersContainer;

    private static void cfgContainerLoad(String temp){
        String[] subStr = temp.split("\n");
        System.out.println("TEMP = " + temp);
        for (String str : subStr) {
            if (str.contains("Cars:")){
                System.out.println("aaaaaaaaaaaaaaaaa = " + str.replace("Cars:", "").trim());
                CarsContainerName = str.replace("Cars:", "").trim();
            }
            else if (str.contains("Orders:")){
                System.out.println("bbbbbbbbbbbbbbbbbbb = " + str.replace("Cars:", "").trim());
                OrdersContainerName = str.replace("Orders:", "").trim();
            }
        }

        ProfileImpl prof1 = new ProfileImpl(true);
        ProfileImpl prof2 = new ProfileImpl(true);
        prof1.setParameter(prof1.CONTAINER_NAME, CarsContainerName);
        prof2.setParameter(prof2.CONTAINER_NAME, OrdersContainerName);        
        Runtime instance = Runtime.instance();
        carsContainer = instance.createAgentContainer(prof1);
        ordersContainer = instance.createAgentContainer(prof2);
    } 
    
    private static void cfgLoadState(String temp, Monitor monitor){
        String[] subStr = temp.split("\n");

        for (String str : subStr) {
            if (str.contains("StartTime:")){
                monitor.nowTime = Integer.valueOf(str.replace("StartTime:", "").trim());
            }
        } 
    }
    private static void cfgOrderLoad(String temp){
        while (temp.contains("Create{")){
            
            int q1 = temp.indexOf("Create{");
            int q2 = temp.indexOf("}Create")  + "}Create".length();

            String tempCreate = temp.substring(q1, q2);
            temp = temp.replace(tempCreate, ""); 
            String[] subStr = tempCreate.split("\n");
            String agName = "";
            Object[] agArgs = new Object[7];
            for (String str : subStr) {
                if (str.contains("id:")){
                    agArgs[0] = Integer.valueOf(str.replace("id:", "").trim());
                }
                else if (str.contains("Name:")){
                    agName = agArgs[0] + str.replace("Name:", "").trim();      
                    agArgs[1] = agName;      
                }
                else if (str.contains("ShopId:")){
                    agArgs[2] = Integer.valueOf(str.replace("ShopId:", "").trim());      
                }
                else if (str.contains("TimeStart:")){ 
                    agArgs[3] = Integer.valueOf(str.replace("TimeStart:", "").trim());      
                }
                else if (str.contains("TimeEnd:")){
                    agArgs[4] = Integer.valueOf(str.replace("TimeEnd:", "").trim());      
                }
                else if (str.contains("Size:")){
                    agArgs[5] = Float.valueOf(str.replace("Size:", "").trim());      
                }
                else if (str.contains("GoodType:")){
                    agArgs[6] = Integer.valueOf(str.replace("GoodType:", "").trim());      
                }
            } 
            try {
                ordersContainer.createNewAgent(agName, "Agents.Order", agArgs).start();          
            } catch (StaleProxyException e) {
                System.out.println("Smth go WRON whil create Order agent " + agName);
                e.printStackTrace();
            }      
        }
    }
    private static void cfgCarLoad(String temp){

        while (temp.contains("Create{")){


            int q1 = temp.indexOf("Create{");
            int q2 = temp.indexOf("}Create")  + "}Create".length();
           
            String tempCreate = temp.substring(q1, q2);
            temp = temp.replace(tempCreate, "");

            String[] subStr = tempCreate.split("\n");
            String agName = "Car";
            Object[] agArgs = new Object[2];
            for (String str : subStr) {
                if (str.contains("Name:")){
                    agName += str.replace("Name:", "");
                }
                else if (str.contains("id:")){
                    agArgs[0] = Integer.valueOf(str.replace("id:", "").trim());      
                }
                else if (str.contains("loadCapacity:")){
                    agArgs[1] = Float.valueOf(str.replace("loadCapacity:", "").trim());      
                }
            } 
            try {
                carsContainer.createNewAgent(agName, "Agents.Car", agArgs).start();         
            } catch (StaleProxyException e) {
                System.out.println("Smth go WRON whil create Shop agent " + agName);
                e.printStackTrace();
            }      
        }
        System.out.println("Shop end");
    } 
    

    public static void readCfg(Monitor monitor){
        System.out.println("Try read file."); 
        Path filePath = Path.of("D:\\MII\\JADE-bin-4.6.0\\jade\\lib\\data\\cfg");
        String content = "";
        try {
            content = Files.readString(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        {//Name of Containers
            int p1 = content.indexOf("ContainerNames{");
            int p2 = content.indexOf("}ContainerNames");
            String temp = content.substring(p1, p2);

            content = content.replace(temp, "");

            cfgContainerLoad(temp);
        }
        
        System.out.println("Containers Done");
        System.out.println("CarsContainerName = " + CarsContainerName);
        System.out.println("OrdersContainerName = " + OrdersContainerName);

        {
            System.out.println("Start car");
            int p1 = content.indexOf("CarM{");
            int p2 = content.indexOf("}CarM");
            String temp = content.substring(p1, p2);
            content = content.replace(temp, "");
            cfgCarLoad(temp);
            System.out.println("End car");
        }
        {
            System.out.println("Start order");
            int p1 = content.indexOf("OrderM{");
            int p2 = content.indexOf("}OrderM");
            String temp = content.substring(p1, p2);
            content = content.replace(temp, "");
            cfgOrderLoad(temp);
            System.out.println("End order");
        }
        //{
        //    System.out.println("Start state");
        //    int p1 = content.indexOf("State{");
        //    int p2 = content.indexOf("}State");
        //    String temp = content.substring(p1, p2);
        //    content = content.replace(temp, "");
//
        //    cfgLoadState(temp, monitor);
        //    System.out.println("End state");
        //}



    }


}