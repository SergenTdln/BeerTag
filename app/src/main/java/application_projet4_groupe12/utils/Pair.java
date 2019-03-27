package application_projet4_groupe12.utils;

public class Pair {
    private Object a;
    private Object b;

    public Pair(Object a, Object b){
        this.a = a;
        this.b = b;
    }
    public Pair(String a, String b){
        this.a = a;
        this.b = b;
    }

    public boolean isComplete(){
        return ((a!=null) && (b!=null));
    }

    public Object getA() {
        return a;
    }

    public Object getB() {
        return b;
    }

    public void setA(Object a) {
        this.a = a;
    }

    public void setB(Object b) {
        this.b = b;
    }
}
