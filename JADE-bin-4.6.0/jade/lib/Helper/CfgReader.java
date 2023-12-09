package Helper;

import Agents.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import Agents.Shop;
import jade.core.Agent;
import jade.core.ContainerID;
import jade.core.ProfileImpl;
import jade.domain.JADEAgentManagement.CreateAgent;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import jade.core.Runtime;



public class CfgReader {
    
    
    public static String ShopsContainerName;
    public static String CarsContainerName;
    public static String StorageContainerName; 

    public static jade.wrapper.AgentContainer shopsContainer;
    public static jade.wrapper.AgentContainer carsContainer;
    public static jade.wrapper.AgentContainer storageContainer;


    private static void cfgContainerLoad(String temp){
        String[] subStr = temp.split("\n");

        for (String str : subStr) {
            if (str.contains("Shops:")){
                ShopsContainerName = str.replace("Shops:", "");
            }
            else if (str.contains("Cars:")){
                CarsContainerName = str.replace("Cars:", "");
            }
            else if (str.contains("Storage:")){
                StorageContainerName = str.replace("Storage:", "");
            }
        }

        ProfileImpl prof1 = new ProfileImpl(true);
        ProfileImpl prof2 = new ProfileImpl(true);
        ProfileImpl prof3 = new ProfileImpl(true);
        prof1.setParameter(prof1.CONTAINER_NAME, ShopsContainerName);
        prof2.setParameter(prof2.CONTAINER_NAME, CarsContainerName);
        prof3.setParameter(prof3.CONTAINER_NAME, StorageContainerName);        
        Runtime instance = Runtime.instance();
        shopsContainer = instance.createAgentContainer(prof1);
        carsContainer = instance.createAgentContainer(prof2);
        storageContainer = instance.createAgentContainer(prof3);
    } 
    
    private static void cfgCarLoad(String temp){
        while (temp.contains("Create{")){
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            
            int q1 = temp.indexOf("Create{");
            int q2 = temp.indexOf("}Create")  + "}Create".length();
           
            String tempCreate = temp.substring(q1, q2);
            temp = temp.replace(tempCreate, "");

            String[] subStr = tempCreate.split("\n");

            for (String str : subStr) {
                if (str.contains("Name:")){
                    String agName = str.replace("Name:", "");
                    try {
                        carsContainer.createNewAgent(agName, "Agents.Car", null).start();
                         
                    } catch (StaleProxyException e) {
                        System.out.println("Smth go WRON whil create Shop agent " + agName);
                        e.printStackTrace();
                    }
                }
            }       
        }
        System.out.println("Shop end");
    } 
    private static void cfgStorageLoad(String temp){
        String[] subStr = temp.split("\n");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        for (String str : subStr) {
            if (str.contains("Name:")){
                if (str.contains("Name:")){
                    String agName = str.replace("Name:", "");
                    try {
                        storageContainer.createNewAgent(agName, "Agents.Storage", null).start();
                         
                    } catch (StaleProxyException e) {
                        System.out.println("Smth go WRON whil create Storage agent " + agName);
                        e.printStackTrace();
                    }
                }
            }
        }
    } 
    private static void cfgShopsLoad(String temp){
        while (temp.contains("Create{")){
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            
            int q1 = temp.indexOf("Create{");
            int q2 = temp.indexOf("}Create")  + "}Create".length();
            
            String tempCreate = temp.substring(q1, q2);
            temp = temp.replace(tempCreate, "");

            String[] subStr = tempCreate.split("\n");

            for (String str : subStr) {
                if (str.contains("Name:")){
                    String agName = str.replace("Name:", "");

                    try {
                        shopsContainer.createNewAgent(agName, "Agents.Shop", null).start();
                         
                    } catch (StaleProxyException e) {
                        System.out.println("Smth go WRON whil create Shop agent " + agName);
                        e.printStackTrace();
                    }
                }
            }       
        }
        System.out.println("Shop end");
    }


    public static void readCfg(){
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
        System.out.println("ShopsContainerName = " + ShopsContainerName);
        System.out.println("StorageContainerName = " + StorageContainerName);
        System.out.println("CarsContainerName = " + CarsContainerName);



        {
            int p1 = content.indexOf("ShopsM{");
            int p2 = content.indexOf("}ShopsM");
            String temp = content.substring(p1, p2);
            content = content.replace(temp, "");

            cfgShopsLoad(temp);
        }
        {
            System.out.println("Start storage");
            int p1 = content.indexOf("StorageM{");
            int p2 = content.indexOf("}StorageM");
            String temp = content.substring(p1, p2);
            content = content.replace(temp, "");
            cfgStorageLoad(temp);
        }
        {
            System.out.println("Start car");
            int p1 = content.indexOf("CarM{");
            int p2 = content.indexOf("}CarM");
            String temp = content.substring(p1, p2);
            content = content.replace(temp, "");
            cfgCarLoad(temp);
        }


    }


}