package com.example.flower;

public class Order {
    private int fid;
    private String fname;
    private float fpriece;
    private int fnumber;
    boolean select;
    public Order(int id,String name,float priece,int number){
        fid=id;
        fpriece=priece;
        fname=name;
        fnumber=number;
        select=true;
    }
    public void setSelect(boolean flag){this.select=flag;}
    public void setFnumber(int i){this.fnumber=i;}
    public int getFid(){
        return fid;
    }
    public String getFname(){return fname;}
    public float getPriece(){
        return fpriece;
    }
    public int getFnumber(){return fnumber;}
    public boolean getSelect(){return select;}
}
