package com.example.flower;

public class Address {
    String name;
    String tel;
    String addressdesc;
    Address(String a,String b,String c){
        this.name=a;
        this.tel=b;
        this.addressdesc=c;
    }
    public  String getName(){
        return name;
    }
    public String getTel(){
        return tel;
    }
    public String getAddressdesc(){
        return addressdesc;
    }
}

