package com.example.flower;

public class Flower {
    private String name;
    private String text;
    private int id;
    private float priece;
    Flower(int fid, String fname, String flowerdes, float fpriece){
        id=fid;
        name=fname;
        text=flowerdes;
        priece=fpriece;
    }
    public String getName(){
        return name;
    }
    public String getText(){
        return text;
    }
    public int getId(){
        return id;
    }
    public float getPriece(){
        return priece;
    }
}
