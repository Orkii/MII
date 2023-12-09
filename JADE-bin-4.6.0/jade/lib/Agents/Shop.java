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


public class Shop extends jade.core.Agent {
    public int id;//position in matrix of distance from 1 (0 is storage)

    protected void setup() { 
        Object[] args = getArguments();

        id = (int) (args[0]);

        System.out.println("Hello World. Im shop. My name is " + getLocalName() + ". My id = " + id + "\n");
        
    }
}
