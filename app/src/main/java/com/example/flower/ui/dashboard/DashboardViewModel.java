package com.example.flower.ui.dashboard;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.flower.Flower;
import com.example.flower.Order;
import com.example.flower.ui.home.HomeFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardViewModel extends ViewModel {

    public static Map<Integer, Integer> map = HomeFragment.map;
    public static Map<Integer, Order> map2 = HomeFragment.map2;
    public static  List<Order> orderList=new ArrayList<>();
    public DashboardViewModel() {
        //if(orderList==null) orderList=getOrderList2();
    }

    public static float getTotmonry(List<Order> a){
        float Totmoney=0;
        for(int i = 0;i < a.size(); i ++){
            Order order2=a.get(i);
            if(order2.getSelect()){
                Totmoney+=order2.getPriece()*order2.getFnumber();
            }
        }
        return Totmoney;
    }

}