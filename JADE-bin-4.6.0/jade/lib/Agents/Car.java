package Agents;



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

    class Pai{
        public Pai(int a, int b){
            L = a;
            R = b;
        }
        public int L;
        public int R;
    }
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
                if (orders.get(j).goodType==ty){
                    or[i]=orders.get(j);
                }
            }
            return or;

        }
        public Boolean isAnyAtThisTime(int t){
            for (MyOrder or : orders) {
                if (or.time == t) return true;
            }
            return false;
        }

        public void destroy(){
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
            for (MyOrder order : ors) {
                sendAnwser(order.name, false);
                load-=order.size;
                orders.remove(order);
            }            
        }
    }

    protected float niceF(ArrayList<Travel> tr_, int beforeTime){//Функция эффективности до определённого времени
        float nice = 0f;
        for (Travel tr : tr_) {
            if (tr.timeStart < beforeTime){
                nice += tr.sizeBeforeTime(beforeTime) / (tr.emptyTimeBeforeTime(beforeTime) + 1);
            }
        }

        return nice;
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
            Boolean fit = tryFit(order);
            sendAnwser(order.name, fit);
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
        //System.out.println("0");
        for (int i = 0; i < size; i++) {//Смотрим существующие путешествия
            //System.out.println("1");
            Travel tr = travels.get(i);
            if (tr.timeStart+TIME_TO_LOADING > time) continue;
            //System.out.println(order.name);
            //System.out.println("tr.load + order.size = " + (tr.load + order.size));
            //System.out.println("loadCapacity = " + loadCapacity);
            if (tr.load + order.size > loadCapacity) {
                System.out.println("Типа");
                int adsadsda = tr.anyTravelWithSameShopType(order);//Вдруг можно кого-нибудь выкинуть
                MyOrder[] a = tr.ordersWithThisType(order.goodType);
                if (a.length == 0) continue;
                for (int k = 0; k < TOTAL_AMOUNT_OF_GOOD_TYPE; k++){
                    if (k == order.goodType) continue;
                    MyOrder[] ors = tr.ordersWithThisType(k);
                    if (ors.length < a.length+1){
                        tr.remove(ors);
                        order.time = a[0].time;
                        return true;
                    }
                }
                

                if (adsadsda == -1) continue;
            }
            if (tr.checkForType(order) == false) continue;
            {//Ели есть тот же магазин, то добавляем
                //System.out.println("2");
                int adsadsda = tr.anyTravelWithSameShopType(order);
                if (adsadsda != -1){
                    order.time = adsadsda;
                    tr.addOrder(order);
                    order.deliver = tr;
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
                    return true;
                }
            }
            //System.out.println("9");

            //order.time = time;
            //tr.addOrder(order);
            //order.deliver = tr;
            //checkForCollision();
            return false;
        }
        
        
        return false;
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
            return true;
        }
        //System.out.println("-6");
        //System.out.println("startFind = " + startFind);
        //System.out.println("endFind = " + endFind);
        for (int i = startFind; i < endFind; i++) {
            if (tryFitAtTime(order, i) == true){
                return true;
            }
        }
        //System.out.println("-7");







        return false;
        /*
        if (travels.isEmpty() == true){//Если нет отправок
            if (fitEmpty(order) == true){
                System.out.println(getLocalName() + " Было пусто " + order.name);
                return true;
            }
        }

        if (travels.get(0).timeStart > order.timeStart){//Этот товар может уехать раньше, чем первое путешествие
            System.out.println("State 2");
            for (Travel travel : travels) {
                travel.destroy();
            }
            travels = new ArrayList<Travel>();
            Travel firstTravel = new Travel();
            firstTravel.timeStart = order.timeStart - TIME_TO_LOADING - TIME_TO_DELIVER;
            if (firstTravel.timeStart < 0) firstTravel.timeStart = 0;
            order.time = order.timeStart;
            firstTravel.orders.add(order);
            return true;
        }
        {//Пытаемя засунуть к тому же магазину
            if (fitSameShop(order) == true){
                System.out.println(getLocalName() + " Засунули в тот же магазин " + order.name);
                return true;
            }
        }
        System.out.println("State 4");
        for (int i = 0; i < travels.size(); i++) {
            if(travels.get(i).checkForType(order) == true) {//Попытка засунуть в первый попавшийся
                if (travels.get(i).load + order.size <= loadCapacity){//Если там есть место
                    System.out.println("Записал " + order.name);
                    travels.get(i).addOrder(order);
                    checkForCollision();
                    return true;
                   
                }
            }
        }
        {//Делаем новый путешествие
            Travel tr = new Travel();
            tr.timeStart=travels.get(travels.size()-1).timeEnd();//И начинается оно после последнего
            System.out.println("Записал " + order.name);
            tr.addOrder(order);
            if (tr.timeEnd() > TIME_IN_DAY){//оказалось, что времени сегодня уже не хватает
                return false;
            }
            

        }
        System.out.println("State 5");
        
        return false;
        */
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
