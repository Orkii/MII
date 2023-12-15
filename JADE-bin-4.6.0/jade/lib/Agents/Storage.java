package Agents;


import java.util.ArrayList;
import java.util.List;

import Helper.Order;
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
import jade.lang.acl.ACLMessage;

public class Storage extends jade.core.Agent{

    List<Order> orders;// = new ArrayList<Order>();
    float[][] distance;
    List<Car> cars;


    protected void setup() { 
        System.out.println("Hello World. Im storage. My name is "+ getLocalName());

        
        orders = new ArrayList<Order>();
        cars = new ArrayList<Car>();
        
        while (true){
            ACLMessage msg = receive();
            if (msg != null) {
                handleMessage(msg);
            }
        }
    }



    protected void handleMessage(ACLMessage message) {
        System.out.println("Storage try get message"); 
        String content = message.getContent();
        System.out.println("Получено информационное сообщение: " + content);


        String[] subStr = content.split("\n");
        if (subStr[0].toLowerCase().contains("car")){
            Car car = new Car();
            car.fromString(subStr[1]);
            cars.add(car);
        }
        else if(subStr[0].toLowerCase().contains("order")){
            Order order = new Order();
            order.fromString(subStr[1]);
            orders.add(order);
        }
        else if(subStr[0].toLowerCase().contains("distance")){
            Order order = new Order();
            System.out.println("subSTR len = " + (subStr.length - 1));
            String[] dist = new String[subStr.length - 1];
            for (int i = 1; i < subStr.length; i++){
                dist[i-1] = subStr[i];
            }

            distance = distanceFromString(dist);
        }
        else if(subStr[0].toLowerCase().contains("show all")){
            System.out.println("Cars:"); 
            for (Car car : cars) {
                System.out.println(car); 
            }
            System.out.println("Orders:"); 
            for (Order order : orders) {
                System.out.println(order); 
            }
            System.out.println("Distance:");
            for (int i = 0; i < distance.length; i++){
                for (int j = 0; j < distance.length; j++){
                    System.out.print(distance[i][j] + " ");
                }
                System.out.println();
            }
        }

    }
    public float[][] distanceFromString(String[] a){
        float[][] distance1;
        //String[] a = str.split("\n");
        String[][] b = new String[a.length][];
        System.out.println("make distanceFromString:");
        System.out.println("make = " + a.length);
        for (int i = 0; i < a.length; i++) {
            System.out.println(i);
            b[i] = a[i].split(" ");
        }
        

        distance1 = new float[b.length][b.length];
        for (int i = 0; i < b.length; i++){
            for (int j = 0; j < b.length; j++){
                distance1[i][j] = Float.parseFloat(b[i][j]);
            }   
        }
        return distance1;
    }
    
}
