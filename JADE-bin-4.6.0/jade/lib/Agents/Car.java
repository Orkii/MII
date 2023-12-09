package Agents;



import jade.core.Agent;
import jade.core.AgentContainer;
import jade.core.Runtime;
import jade.core.ContainerID;
import jade.core.Profile;
import jade.core.behaviours.ActionExecutor;
import jade.core.behaviours.OutcomeManager;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.JADEAgentManagement.CreateAgent;
import jade.domain.JADEAgentManagement.JADEManagementOntology;


public class Car extends jade.core.Agent {
    protected void setup() { 
        System.out.println("Hello World. Im car");
        System.out.println("My name is "+ getLocalName()); 
       
    }
}
