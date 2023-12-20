package Agents;


import Helper.CfgReader;
import Helper.Pai;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import java.util.ArrayList;



public class Car extends jade.core.Agent {
    public int id;
    public float loadCapacity;

    public boolean isINeedTryGetOrders = true;


    final int TIME_TO_WAIT_BETWEEN_RECEIVE = 100;
    final int TIME_TO_LOADING = 2;
    final int TIME_TO_DELIVER = 1;
    final int TIME_IN_DAY = 50;
    final int TOTAL_AMOUNT_OF_GOOD_TYPE = 10;
    final int BIGGETS_TIME_GAP = 8;



    class MyOrder {
        public int id;
        public String name;
        public int shopId;
        public int timeStart;
        public int timeEnd;
        public float size;
        public int goodType;  

        public Travel deliver;//В какой я доставке
        public int time;//Время прибытия

        public String toString() {
            return  id + " " +
                    name + " " +
                    shopId + " " +
                    timeStart + " " +
                    timeEnd + " " +
                    size + " " +
                    goodType ;
        }
        public void fromString(String str){
            String[] args = str.split(" ");
            id = Integer.parseInt(args[0]);
            name = args[1];
            shopId = Integer.parseInt(args[2]);
            timeStart = Integer.parseInt(args[3]);
            timeEnd = Integer.parseInt(args[4]);
            size = Float.parseFloat(args[5]);
            goodType = Integer.parseInt(args[6]);
        }
    } 
    

    class Travel{
        
        public ArrayList<MyOrder> orders = new ArrayList<MyOrder>();
        public int load;
        public int timeStart;
        public Boolean addOrder(MyOrder order){
            load += order.size;
            System.out.println("added SMTH now load = " + load);
            if (load > loadCapacity){
                //load-=loadCapacity;
                System.out.println("МАШИНА ПЕРЕПОЛНЕНА");
                throw new RuntimeException("Груз слишком тяжёлый");
                //return false;
            }
            orders.add(order);

            
            return true;

        }
        public int timeEnd(){
            int latestTime = 0;
            for (MyOrder order : orders) {
                if (latestTime < order.time){
                    latestTime = order.time;
                }
            }
            return latestTime;
        }
        public int sizeBeforeTime(int beforeTime){//Какая загруженость до определённого времени
            int totalSize = 0;
            for (MyOrder order : orders) {
                if (beforeTime >= order.time){
                    totalSize += order.size;
                }
            }
            return totalSize;
        }
        public int countOfShop(int id){
            int count = 0;
            for (MyOrder order : orders) {
                if (order.shopId == id){
                    count++;
                }
            }
            return count;
        }
        public int emptyTimeBeforeTime(int beforeTime){//Какая загруженость до определённого времени
            int totalEmptyCell = timeEnd() - timeStart - TIME_TO_LOADING - TIME_TO_DELIVER;
            for (MyOrder order : orders) {
                if (beforeTime >= order.time){
                    totalEmptyCell--;
                }
            }
            return totalEmptyCell;
        }
        public Boolean checkForType(MyOrder order){//True = Можно добавить   False = Нельзя добавить
            for (MyOrder or : orders) {
                for (int i = 0; i < CfgReader.notMix.length; i++) {
                    if ((CfgReader.notMix[i].L == or.goodType) && (CfgReader.notMix[i].R == order.goodType)) return false;
                    if ((CfgReader.notMix[i].R == or.goodType) && (CfgReader.notMix[i].L == order.goodType)) return false;  
                }

            }
            return true;
        }
        public MyOrder[] ordersWithThisType(int ty){
            //MyOrder[] or = new MyOrder[];
            int amount = 0;
            for (MyOrder order : orders) {
                if (order.goodType == ty){
                    amount++;
                }
            }
            MyOrder[] or = new MyOrder[amount];
            for (int i = 0,j=0; i < amount;j++){
                //System.out.println("i = " + i);
                //System.out.println("j = " + j);
                if (orders.get(j).goodType==ty){

                    or[i]=orders.get(j);
                    i++;
                }
            }
            return or;

        }
        public MyOrder isAnyAtThisTime(int t){
            for (MyOrder or : orders) {
                if (or.time == t) return or;
            }
            return null;
        }

        public void destroy(){
            System.out.println("DESTROY");
            for (MyOrder or : orders) {
                sendAnwser(or.name, false);
            }

        }
        public int anyTravelWithSameShopType(MyOrder or){//Есть ли хоть в одном путешествии этот магазин? -1 = Нет  0,1,2.. = То время
        
            for (MyOrder ord : orders) {
                if (ord.shopId == or.shopId) return ord.time;
            } 
            return -1;
        }
        public void remove(MyOrder[] ors){
            System.out.println("REMOVE");
            for (MyOrder order : ors) {
                sendAnwser(order.name, false);
                load-=order.size;
                orders.remove(order);
            }            
        }
    }



    private ArrayList<Travel> travels;
    protected void setup() {// args == id, Load capacity
        Object[] args = getArguments();
        id = (int)args[0];
        loadCapacity = (float)args[1];
        System.out.println("Im car. My name is "+ getLocalName() + ". My id = " + id + " My loadCapacity = " + loadCapacity);
        System.out.println();
        
        travels = new ArrayList<Travel>();



        while (isINeedTryGetOrders == true){
            ACLMessage msg = getMessage();
            handleMessage(msg);
        }
      
    }

    protected void handleMessage(ACLMessage msg) {
        // Обработка информационного сообщения
        String text = msg.getContent();
        System.out.println(getLocalName() + " GET: " + text);
        
        if (text.toLowerCase().contains("show all")){
            System.out.println(this);
        }

        if (msg.getPerformative() == ACLMessage.REQUEST) {//Время получено
            MyOrder order = new MyOrder();
            order.fromString(text);
            Boolean fit = tryFitNew(order);
            sendAnwser(order.name, fit);
        }
        else if(msg.getPerformative() == ACLMessage.CANCEL){
            try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }
            System.out.println("TRAVELS = " + travels.size());
            for (int index = 0; index < travels.size(); index++) {
                Travel tr = travels.get(index);
                for (MyOrder or : tr.orders) {
                    System.out.println(getLocalName() + " В поездке N:" + index + " отвозит " + or.name + " время = " + or.time);
                }
            }
            travels.clear();
        }



        
    }
    protected void checkForCollision(){
        for (int i = 0; i < travels.size()-1; i++) {
            if(travels.get(i).timeEnd() >= travels.get(i+1).timeStart) {//SMTH very bad is going on
                i++;
                int temp = i;
                for (; i < travels.size(); i++) {
                    travels.get(temp).destroy();
                    travels.remove(temp);
                }   
            }
        }  
    }

    protected Boolean fitEmpty(MyOrder order, int time){
        Travel tr = new Travel();
        order.time = time;
        order.deliver = tr;

        tr.timeStart = time-TIME_TO_LOADING-TIME_TO_DELIVER;
        tr.addOrder(order);
        travels.add(tr);
    
        return true;
    }


    protected Boolean tryFitAtTime(MyOrder order, int time){
        int size = travels.size();

        //System.out.println(getLocalName() + " " + order.name + "  tryFitAtTime" + time);

        for (int i = 0; i < size; i++) {//Смотрим существующие путешествия

            Travel tr = travels.get(i);
            if (tr.timeStart+TIME_TO_LOADING > time) continue;

            if (tr.load + order.size > loadCapacity) {
                //System.out.println("Типа");
                int adsadsda = tr.anyTravelWithSameShopType(order);//Вдруг можно кого-нибудь выкинуть
                MyOrder[] a = tr.ordersWithThisType(order.goodType);
                if (a.length == 0) continue;
                //System.out.println("Типа1");
                int mas = 0;
                for (int j = 0; j < a.length; j++) {
                    mas += a[j].size;
                }
                if (mas+order.size > loadCapacity) continue;

                for (int k = 0; k < TOTAL_AMOUNT_OF_GOOD_TYPE; k++){
                    if (k == order.goodType) continue;
                    MyOrder[] ors = tr.ordersWithThisType(k);
                    if (ors.length < a.length+1){
                        tr.remove(ors);
                        order.time = a[0].time;
                        tr.addOrder(order);
                        System.out.println(getLocalName() + " " );
                        System.out.println(getLocalName() + " " + order.name + "  STATE1");
                        return true;
                    }
                }

                if (adsadsda == -1) continue;
            }

            
            if (tr.checkForType(order) == false) continue;
            {//Ели есть тот же магазин, то добавляем
                System.out.println("2");
                int adsadsda = tr.anyTravelWithSameShopType(order);
                if (adsadsda != -1){
                    order.time = adsadsda;
                    tr.addOrder(order);
                    order.deliver = tr;
                    System.out.println(getLocalName() + " " + order.name + "  STATE2");
                    return true;
                }
            }
            //System.out.println("3");
            
            if (i != size-1){//если не последнияя поездка
                //System.out.println("4");
                if (time > tr.timeEnd()){//Добавляем в конец текущей
                    //System.out.println("5");
                    int dist = travels.get(i+1).timeStart - 1;
                    if ((time <= dist) && (time > tr.timeStart+TIME_TO_LOADING)){
                        //System.out.println("6");
                        order.time = time;
                        tr.addOrder(order);
                        order.deliver = tr;
                        System.out.println(getLocalName() + " " + order.name + "  STATE3");
                        return true;
                    }
                }                
            }
            else if(i == size-1){
                //System.out.println("7");
                if (time > tr.timeEnd()){
                    //System.out.println("8");
                    order.time = time;
                    tr.addOrder(order);
                    order.deliver = tr;
                    System.out.println(getLocalName() + " " + order.name + "  STATE4");
                    return true;
                }
            }

        }
        //Новое путешествие
        Travel tr = new Travel();
        tr.timeStart = travels.getLast().timeEnd()+1;

        if (tr.timeStart + TIME_TO_LOADING + TIME_TO_DELIVER >=TIME_IN_DAY) return false;
        if (time < tr.timeStart)    return false;
        
        tr.addOrder(order);
        order.deliver = tr;
        order.time = time;
        System.out.println(getLocalName() + " " + order.name + "  STATE5");
        return true;
    }

    protected Boolean tryFit(MyOrder order){
        int startFind = order.timeStart;
        int endFind = order.timeEnd;



        //System.out.println("-0");
        if (order.size > loadCapacity){
            return false;
        }


        if (startFind > endFind){
            //System.out.println("-1");
            System.out.println("Заказ начинают принимать позже конца.");
            throw new RuntimeException("Заказ начинают принимать позже конца.");
        }
        //System.out.println("TIME_IN_DAY = " + TIME_IN_DAY);
        if (endFind > TIME_IN_DAY){
            //System.out.println("-2");
            endFind = TIME_IN_DAY;
        }

        if (startFind < TIME_TO_DELIVER + TIME_TO_LOADING){
            //System.out.println("-3");
            startFind = TIME_TO_DELIVER + TIME_TO_LOADING;
        }
        //////
        //System.out.println("-4");
        if (travels.isEmpty() == true){//Первый
            //System.out.println("-5");
            System.out.println(getLocalName() + " Было пусто " + order.name);
            fitEmpty(order, startFind);
            //sendAnwser(order.name, true);
            return true;
        }
        for (int i = startFind; i < endFind; i++) {
            if (tryFitAtTime(order, i) == true){
                return true;
            }
            
        }
        System.out.println(getLocalName() + " " + order.name + " RETURN STATE FALSE");
        //System.out.println("-7");
          return false;

    }



    protected Boolean tryFitNew(MyOrder order){
        int startFind = order.timeStart;
        int endFind = order.timeEnd;



        //System.out.println("-0");
        if (order.size > loadCapacity){
            return false;
        }


        if (startFind > endFind){
            //System.out.println("-1");
            System.out.println("Заказ начинают принимать позже конца.");
            throw new RuntimeException("Заказ начинают принимать позже конца.");
        }
        //System.out.println("TIME_IN_DAY = " + TIME_IN_DAY);
        if (endFind > TIME_IN_DAY){
            //System.out.println("-2");
            endFind = TIME_IN_DAY;
        }

        if (startFind < TIME_TO_DELIVER + TIME_TO_LOADING){
            //System.out.println("-3");
            startFind = TIME_TO_DELIVER + TIME_TO_LOADING;
        }
        
        if (travels.isEmpty() == true){//Первый
            //System.out.println("-5");
            System.out.println(getLocalName() + " Было пусто " + order.name);
            fitEmpty(order, startFind);
            //sendAnwser(order.name, true);
            System.out.println("CREATE Empety. + ");
            return true;
        }
        
        for (int time = startFind; time < endFind; time++) {
            int badEndingCount = 0;
            for (int i = 0; i < travels.size(); i++) {
                Travel tr = travels.get(i);
                if (tr.checkForType(order) == false) {
                    //badEndingCount++;
                    continue;
                }
                if (tr.load + order.size > loadCapacity) {
                    //badEndingCount++;
                    continue;
                }
                if (tr.timeStart > time) continue;
                if (tr.timeStart + BIGGETS_TIME_GAP < time) {
                    badEndingCount++; 
                    continue;
                }
                
                
                MyOrder or = tr.isAnyAtThisTime(time); 
                if (or != null){
                    //System.out.println("AAAAAAAAAA 1");
                    if (or.shopId == order.shopId) {
                        //System.out.println("AAAAAAAAAA 2");
                        tr.addOrder(order);
                        order.deliver = tr;
                        order.time = time;
                        System.out.println("CREATE 1. + " + time);
                        return true;
                        //sendAnwser(order.name, true);;
                    }
                }
                if (or == null){
                    //System.out.println("AAAAAAAAAA 3");
                    Boolean evBAD = false;

                    for (int j = 0; j < travels.size(); j++) {
                        if (i == j) continue;

                        if (travels.get(i).timeStart < travels.get(j).timeStart){
                            if (time < travels.get(j).timeStart){
                                continue;
                            }
                            else{
                                evBAD = true;
                                break;
                            }
                        }
                    }

                    //if (evBAD == false){
                    //    System.out.println("CREATE NEW TRAVEL 2 . + " + time);
//
                    //    //travels.add(tr);
                    //    tr.addOrder(order);
                    //    order.deliver = tr;
                    //    order.time = time;
                    //    return true;
                    //}
                    //sendAnwser(order.name, true);; 
                }                
            }
            //System.out.println("badEndingCount = " + badEndingCount + " travels.size() " + travels.size());

             
            int latestTime = 0;
            for (int i = 0; i < travels.size(); i++) {
                if (travels.get(i).timeEnd() > latestTime){
                    latestTime = travels.get(i).timeEnd();
                }
            }
            if (latestTime + 2 < time){
                System.out.println("CREATE NEW TRAVEL 1 ."  + time);
                Travel tr11 = new Travel();
                tr11.timeStart = time - TIME_TO_DELIVER - TIME_TO_LOADING;

                if (latestTime < time){
                    travels.add(tr11);
                    tr11.addOrder(order);
                    order.deliver = tr11;
                    order.time = time;

                    return true;
                }
            }

            
        }


        //Travel tr = new Travel();
        //tr.timeStart = time - TIME_TO_DELIVER - TIME_TO_LOADING;
        //tr.timeStart = time - TIME_TO_DELIVER - TIME_TO_LOADING;
        
        return false;//Время кончилось
    }

 











    @Override
    public String toString() {
        return id + " " +  getLocalName() + " " + loadCapacity;
    }
    public void fromString(String str){
        String[] subStr = str.split(" ");
        id = Integer.valueOf(subStr[0].trim());
        loadCapacity = Float.valueOf(subStr[1].trim());
    }

    protected ACLMessage getMessage(){
        ACLMessage msg = receive();
        while (msg == null) {
            try { Thread.sleep(TIME_TO_WAIT_BETWEEN_RECEIVE); } catch (InterruptedException e) { e.printStackTrace(); }
            msg = receive();
        }
        return msg;
    }


    
    protected void sendAnwser(String orderName, Boolean answer){
        if (answer == true){
            send("CONFIRM", ACLMessage.CONFIRM, orderName);
        }
        else{
            send("DISCONFIRM", ACLMessage.DISCONFIRM, orderName);
        }
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
