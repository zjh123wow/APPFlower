package com.example.flower.ui.home;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.flower.DBUtils;
import com.example.flower.Flower;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeViewModel extends ViewModel {

   // private MutableLiveData<String> mText;
    //Map<Integer, Integer> map = new HashMap<Integer,Integer>();
    private List<Flower> flowerList=new ArrayList<>();

    public HomeViewModel() {

    }
    public List<Flower> getFlowerList(Context context){
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                flowerList= DBUtils. initflower(context);
            }
        });
        thread.start();
        while (thread.isAlive());
        return flowerList;
    }
}