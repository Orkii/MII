package Agents;




import java.util.List;

import Helper.Order;
import jade.core.AID;


import jade.lang.acl.ACLMessage;
import java.util.Timer;
import java.util.TimerTask;

public class Shop extends jade.core.Agent {
    public int id;//position in matrix of distance from 1 (0 is storage)
    public int timeStart;
    public int timeEnd;
    List<Order> orders;// = new ArrayList<Order>();
    protected void setup() {//args = id, timeStart, timeEnd, list of orders
        Object[] args = getArguments();

        id = (int) (args[0]);
        timeStart = (int) (args[1]);
        timeEnd = (int) (args[2]);
        orders = (List<Order>) (args[3]);
        for (Object order : orders) {
            ((Order)order).shopId = id;
            ((Order)order).timeStart = timeStart;
            ((Order)order).timeEnd = timeEnd;
        }
        System.out.println("Hello World. Im shop. My name is " + getLocalName() + ". My id = " + id + 
        ".My time is from " + timeStart + " to " + timeEnd +"\n" + "My order = " + orders.getFirst());
        //System.out.println("My order = " + orders.getFirst());

        //Timer timer = new Timer();
        //timer.schedule(new TimerTas(), 5000); // Запуск задачи через 5 секунд
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        while (true){
            ACLMessage msg = receive();
            if (msg != null) {
                handleMessage(msg);
            }
        }
    }



    public void sendMessageAboutOrders() {
        System.out.println("Try send message");

        for (Order order : orders) {
            ACLMessage message = new ACLMessage(ACLMessage.INFORM);
            message.addReceiver(new AID("mainStorage", AID.ISLOCALNAME));
            message.setContent("order\n" + order.toString());
            send(message);
        }
        
    }
    
    protected void handleMessage(ACLMessage message) {
        if (message.getPerformative() == ACLMessage.INFORM) {
            // Обработка информационного сообщения
            String content = message.getContent();
            System.out.println("Получено информационное сообщение: " + content);
            if (content.toLowerCase() == "show all"){
                System.out.println(this);
            }

        } else {
  
        }
    }
}
