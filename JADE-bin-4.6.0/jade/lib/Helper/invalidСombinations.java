package Helper;

import java.util.ArrayList;

import java.util.List;


class Pair{
    public Pair(int x_, int y_){
        x = x_;
        y = y_;
    }
    public int x;
    public int y;
}

public class invalidСombinations {
    public static List<Pair> combo;
    
    public static void init(){
        combo = new ArrayList<Pair>();
        Pair v = new Pair(1,2);
        combo.add(v);
    }
}
