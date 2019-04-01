package application_projet4_groupe12.utils;

public class Triplet {
    private Object a;
    private Object b;
    private Object c;

    public Triplet(Object a, Object b, Object c){
        this.a = a;
        this.b = b;
        this.c = c;
    }
    public Triplet(String a, String b, String c){
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public boolean isComplete(){
        return ((a!=null) && (b!=null) && (c!=null));
    }

    public Object getA() {
        return a;
    }

    public Object getB() {
        return b;
    }

    public Object getC(){
        return c;
    }

    public void setA(Object a) {
        this.a = a;
    }

    public void setB(Object b) {
        this.b = b;
    }

    public void setC(Object c){
        this.c = c;
    }
}