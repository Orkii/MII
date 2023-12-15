package Helper;

public class Order{
    public Order(){

    }
    public int      id;
    public int      timeStart;
    public int      timeEnd;
    public int      shopId;
    public int      goodType;
    public float    size;

    @Override
    public String toString() {
        return id + " " + timeStart + " " + timeEnd + " " + shopId + " " + goodType + " " + size;
        //return "Order id:" + shopId +
        //        " goodType:" + goodType + 
        //        " Size:" + size +
        //        " timeStart:" + timeStart +
        //        " timeEnd:" + timeEnd;
    }
    public void fromString(String str){
        String[] subStr = str.split(" ");
        id = Integer.valueOf(subStr[0].trim());
        timeStart = Integer.valueOf(subStr[1].trim());
        timeEnd = Integer.valueOf(subStr[2].trim());
        shopId = Integer.valueOf(subStr[3].trim());
        goodType = Integer.valueOf(subStr[4].trim());
        size = Float.valueOf(subStr[5].trim());
    }
}