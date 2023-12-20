package Agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.Location;
import jade.core.event.ContainerAdapter;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.JADEAgentManagement.QueryAgentsOnLocation;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;

import java.util.ArrayList;
    
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.*;
import jade.content.AgentAction;

import jade.domain.JADEAgentManagement.KillAgent;

import jade.lang.acl.ACLMessage;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.basic.Action;
import jade.core.behaviours.OneShotBehaviour;

//import jade.domain.JADEAgentManagement;
//import jade.domain;

public class Order extends jade.core.Agent{
    final int TIME_TO_WAIT_BETWEEN_RECEIVE = 100;



    public int id;
    public String name;
    public int shopId;
    public int timeStart;
    public int timeEnd;
    public float size;
    public int goodType;  
    public boolean isINeedTryFindCar = true;

    Boolean die = false;

    boolean haveTime = false;
    boolean tryFindAnother = false;
    String myCar = "";
    String[] cars;
    protected void setup() {// args == id, Load capacity, speed
        Object[] args = getArguments();
        id = (int)args[0];
        name = (String)args[1];
        shopId = (int)args[2];
        timeStart = (int)args[3];
        timeEnd = (int)args[4];
        size = (float)args[5];
        goodType = (int)args[6];
        
        System.out.println("Order:" + this);
       

        QueryAgentsOnLocation ca = new QueryAgentsOnLocation();

        
        AgentContainer carsContainer = new AgentContainer(null, null, "Cars");// получить контейнер

        AMSAgentDescription [] agents = null;

        ArrayList<String> carsTemp = new ArrayList<String>();

        try {
            SearchConstraints c = new SearchConstraints();
            c.setMaxResults (-1L);
            agents = AMSService.search( this, new AMSAgentDescription (), c );
        }
        catch (Exception e) { }

        for (int i=0; i<agents.length;i++){
            AID agentID = agents[i].getName();
            if(agentID.getLocalName().contains("Car")){
                carsTemp.add(agentID.getLocalName());
            }
        }
        
        cars = new String[carsTemp.size()];
        for (int i=0; i<carsTemp.size();i++){
            cars[i] = carsTemp.get(i);
        }
        System.out.println("Заказ = " + this);

        doTryFindPlaceInCar();

        /*
        for (int i=0; i<cars.length;i++){

            if (haveTime == true){
                System.out.println("FOUND1");
                break;    
            }

            System.out.println("\n\nORDER STATE 0\n\n");


            send(size + " " + shopId + " " + goodType, ACLMessage.REQUEST, cars[i]);
            ACLMessage msg = receive();
            while (msg == null) {
                try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
                msg = receive();
            }
            
            //handleMessage(msg);

            

            System.out.println("adasdasd = " + msg.getPerformative());
            if ((msg.getPerformative() == ACLMessage.CONFIRM) || (msg.getPerformative() == ACLMessage.CANCEL)) {//Время получено
                System.out.println(getLocalName() + " Получено ответ: CONFIRM1");

                System.out.println("S1");
                System.out.println("haveTime = " + haveTime);
                while (haveTime == false){
                    System.out.println("S2");
                    String content = msg.getContent();
                    int mayBeTime = Integer.parseInt(content);
                    System.out.println("S3");

                    if ((mayBeTime>timeStart) && (mayBeTime < timeEnd)){//Время норм
                        System.out.println("S4");
                        System.out.println(getLocalName() + " Получил время");
                        send("", ACLMessage.CONFIRM, cars[i]);
                        System.out.println(getLocalName() + " Время норм");
                        haveTime = true;
                    }
                    else{
                        send("", ACLMessage.DISCONFIRM, cars[i]);
                        msg = null;
                        while (msg == null) {
                            try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
                            msg = receive();
                        }
                    }
                    

                }
                System.out.println("Q1");



                    
                

                if (haveTime == false){
                    send("NONE", ACLMessage.REFUSE, cars[i]);
                }
            }
            else if (msg.getPerformative() == ACLMessage.DISCONFIRM) {//Время откланено
                System.out.println(getLocalName() + " Получено ответ: DISCONFIRM");
            }
            else if (msg.getPerformative() == ACLMessage.REFUSE) {//Время забрали
                System.out.println(getLocalName() + " Получено ответ: REFUSE");
            } 
            else if (msg.getPerformative() == ACLMessage.AGREE) {//Время подтверждено
                System.out.println(getLocalName() + " Получено ответ: AGREE");
            } 
        }

        */
        //System.out.println(getLocalName() + " ЧЁ");
        getMessage();

    }
    @Override
    public String toString() {
        return  id + " " +
                name + " " +
                shopId + " " +
                timeStart + " " +
                timeEnd + " " +
                size + " " +
                goodType ;
    }

    protected void doTryFindPlaceInCar(){
        System.out.println(getLocalName() + " doTryFindPlaceInCar");
        while (isINeedTryFindCar == true){
            for (int i=0; i<cars.length;i++){
                if (die == true) return;
                while (haveTime == true){//Если у нас есть машина, то ждём пока нас выпрут
                    System.out.println(getLocalName() + " Записался");
                    handleMessage(getMessage());
                    if (die == true) return;
                }
                System.out.println(getLocalName() + " Хочу записаться");
                sendAsk(cars[i]);
                ACLMessage msg = getMessage();
                handleMessage(msg);
                if (die == true) return;
            }
            
        }
    }


    protected void handleMessage(ACLMessage message) {//Меняет haveTime
        // Обработка информационного сообщения
        String text = message.getContent();
        System.out.println(getLocalName() + " GET: " + text);
        if (text.toLowerCase().contains("show all")){
            System.out.println(this);
        }
        if (message.getPerformative() == ACLMessage.CONFIRM){
            haveTime = true;
        }
        else if (message.getPerformative() == ACLMessage.DISCONFIRM){
        
            haveTime = false;
        }
        else if(message.getPerformative() == ACLMessage.CANCEL){
            System.out.println(getLocalName() + " Time =  " + haveTime);
            if (haveTime == true){
                try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
                System.out.println(getLocalName() + " доставлен");
                isINeedTryFindCar = false;
                die = true;
                this.doDelete();
            }
            else{
                isINeedTryFindCar = false;
            }

        }
        else if (message.getPerformative() == ACLMessage.INFORM){
            isINeedTryFindCar = true;
            doTryFindPlaceInCar();
        }
    }

    protected void sendAsk(String carName) {//Спрашиваем у машины прокатиться 
        System.out.println(getLocalName() + " ASK " + carName);
        send(toString(), ACLMessage.REQUEST, carName);
    }

    protected ACLMessage getMessage(){
        ACLMessage msg = receive();
        while (msg == null) {
            try { Thread.sleep(TIME_TO_WAIT_BETWEEN_RECEIVE); } catch (InterruptedException e) { e.printStackTrace(); }
            msg = receive();
        }
        return msg;
    }

    private void send(String message, int theme, String receiver){
            ACLMessage message1 = new ACLMessage(theme);
            message1.addReceiver(new AID(receiver, AID.ISLOCALNAME));
            //message1.addReceiver(receiver);
            message1.setContent(message);
            send(message1);
    }
    private void send(String message, int theme, String[] receiver){
            ACLMessage message1 = new ACLMessage(theme);
            for (String aid : receiver) {
                message1.addReceiver(new AID(aid, AID.ISLOCALNAME));
            }
            message1.setContent(message);
            send(message1);
    }
}
