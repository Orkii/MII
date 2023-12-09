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
    } 
    private static void cfgShopsLoad(String temp){
        System.out.println("Found ShopsM{");

        while (temp.contains("Create{")){
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            

            System.out.println("\nFound Create{");
            int q1 = temp.indexOf("Create{");
            int q2 = temp.indexOf("}Create")  + "}Create".length();
            System.out.println("Create tempCreate");

            System.out.println("q1 = " + q1 + " q2 = " + q2);
            
            String tempCreate = temp.substring(q1, q2);

            System.out.println("tempCreate = " + tempCreate);

            System.out.println("Create replace");
            temp = temp.replace(tempCreate, "");

            System.out.println("Going to create a new Shop in container " + ShopsContainerName + " right now...");


            String[] subStr = tempCreate.split("\n");

            System.out.println("subSTR = " + subStr);

            for (String str : subStr) {
                if (str.contains("Name:")){
                    System.out.println("Found Name:");
                    String agName = str.replace("Name:", "");

                    System.out.println("AgName = " + agName);
                    try {
                        shopsContainer.createNewAgent(agName, "Agents.Shop", null).start();
                         
                    } catch (StaleProxyException e) {
                        System.out.println("Smth go WRON whil create Shop agent " + agName);
                        e.printStackTrace();
                    }
                }
            }       
        }
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
            content.replace(temp, "");

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
        
        System.out.println("Containers Done");
        System.out.println("ShopsContainerName = " + ShopsContainerName);
        System.out.println("StorageContainerName = " + StorageContainerName);
        System.out.println("CarsContainerName = " + CarsContainerName);



        {
            int p1 = content.indexOf("ShopsM{");
            int p2 = content.indexOf("}ShopsM");
            String temp = content.substring(p1, p2);
            content.replace(temp, "");

            System.out.println("Found ShopsM{");

            while (temp.contains("Create{")){
                
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                

                System.out.println("\nFound Create{");
                int q1 = temp.indexOf("Create{");
                int q2 = temp.indexOf("}Create")  + "}Create".length();
                System.out.println("Create tempCreate");

                System.out.println("q1 = " + q1 + " q2 = " + q2);
                
                String tempCreate = temp.substring(q1, q2);

                System.out.println("tempCreate = " + tempCreate);

                System.out.println("Create replace");
                temp = temp.replace(tempCreate, "");

                System.out.println("Going to create a new Shop in container " + ShopsContainerName + " right now...");


                String[] subStr = tempCreate.split("\n");

                System.out.println("subSTR = " + subStr);

                for (String str : subStr) {
                    if (str.contains("Name:")){
                        System.out.println("Found Name:");
                        String agName = str.replace("Name:", "");

                        System.out.println("AgName = " + agName);
                        try {
                            shopsContainer.createNewAgent(agName, "Agents.Shop", null).start();
                             
                        } catch (StaleProxyException e) {
                            System.out.println("Smth go WRON whil create Shop agent " + agName);
                            e.printStackTrace();
                        }

                    }
                }
                
                //shopsContainer.acceptNewAgent(null, ca);
                
            }


        }
    



    }


}