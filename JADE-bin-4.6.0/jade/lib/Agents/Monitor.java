package Agents;

import Helper.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.InputMap;

import jade.core.Agent;
import jade.core.AgentContainer;
import jade.core.Runtime;
import jade.core.ContainerID;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.behaviours.ActionExecutor;
import jade.core.behaviours.OutcomeManager;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.JADEAgentManagement.CreateAgent;
import jade.domain.JADEAgentManagement.JADEManagementOntology;





public class Monitor extends jade.core.Agent { 
    String myContainerName = "";
    protected void setup() { 
        //String containerName = AgentContainer.MAIN_CONTAINER_NAME;
        //String containerName = "Container-1";
        

        System.out.println("Try read file."); 
        Helper.CfgReader.readCfg();
        

        System.out.println("car = " + Helper.CfgReader.CarsContainerName); 
        System.out.println("shop = " + Helper.CfgReader.ShopsContainerName); 
        System.out.println("storage = " + Helper.CfgReader.StorageContainerName); 

        try{
            myContainerName = this.getContainerController().getContainerName();
        }
        catch (Exception e) {
            System.out.println("ERROR: cannot find container."); 
        }
        //String containerName = "TestContainer";
        


        myContainerName = "TEST-Container";

        

        System.out.println("I am monitor. My name is "+ getLocalName() + ". Start all the things"); 

        /* 
        System.out.println("Creating agent!");
		CreateAgent ca = new CreateAgent();

        ca.setAgentName(getLocalName()+"-child");
        ca.setClassName("Agents.TestAgent");
        ca.setContainer(new ContainerID(myContainerName, null));

        ActionExecutor<CreateAgent, Void> ae = new ActionExecutor<CreateAgent, Void>(ca, JADEManagementOntology.getInstance(), getAMS()) {
            @Override
            public int onEnd() {
                int ret = super.onEnd();
                if (getExitCode() == OutcomeManager.OK) {
                    // Creation successful
                    System.out.println("Agent successfully created");
                }
                else {
                    // Something went wrong
                    System.out.println("Agent creation error. "+getErrorMsg());
                }
                return ret;
            }
        };
        addBehaviour(ae);
        */
    }
}