package Agents;

import Helper.*;
import jade.core.AID;
import jade.core.Agent;


import jade.lang.acl.ACLMessage;



public class Monitor extends jade.core.Agent { 
    String myContainerName = "";

    public int nowTime;
    protected void setup() { 
        //String containerName = AgentContainer.MAIN_CONTAINER_NAME;
        //String containerName = "Container-1";
        
        

        System.out.println("Try read file."); 
        Helper.CfgReader.readCfg(this);
        

        System.out.println("car = " + Helper.CfgReader.CarsContainerName); 
        System.out.println("shop = " + Helper.CfgReader.OrdersContainerName); 


        try{
            myContainerName = this.getContainerController().getContainerName();
        }
        catch (Exception e) {
            System.out.println("ERROR: cannot find container."); 
        }
        invalidСombinations.init();
        //String containerName = "TestContainer";
        


        myContainerName = "TEST-Container";

        

        System.out.println("I am monitor. My name is "+ getLocalName() + ". Start all the things"); 
        System.out.println("Time = " + nowTime);
        
        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        message.addReceiver(new AID("mainStorage", AID.ISLOCALNAME));
        send(message);

        
        //while (true){
        //    ACLMessage msg = receive();
        //    if (msg != null) {
        //        handleMessage(msg);
        //    }
        //}

    }

    protected void handleMessage(ACLMessage message) {
        System.out.println("Try get message"); 
        String content = message.getContent();
        System.out.println("BOB get: " + content);
        

    }




}