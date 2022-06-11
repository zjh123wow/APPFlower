package com.example.flower.ui.home;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.flower.AnalysisUtils;
import com.example.flower.DBUtils;
import com.example.flower.Flower;
import com.example.flower.FlowerAdapter;
import com.example.flower.Order;
import com.example.flower.R;
import com.example.flower.databinding.FragmentHomeBinding;
import com.example.flower.ui.dashboard.DashboardViewModel;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private ViewFlipper myViewFlipper;
    private ListView listView;
    private List<Flower> flowerList1=new ArrayList<>();       //flowerList1是删了前三个元素的fowerList
    private List<Flower> flowerList2=new ArrayList<>();
    public static Map<Integer, Integer> map = new HashMap<Integer,Integer>();
    public static Map<Integer, Order> map2=new HashMap<>();
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Log.d("home", "onCreateView: 1");
        flowerList1=homeViewModel.getFlowerList(getContext());
        myViewFlipper=binding.flipper;
        listView=binding.listview1;
        for (int i = 0; i < 3; i++) {
            Flower floweri=(Flower)flowerList1.get(i);
            //动态加载
            myViewFlipper.addView(getImageView(floweri.getId()));
        }

        // myViewFlipper.setFlipInterval(2000);
        myViewFlipper.startFlipping();
        //初始化
        initFlower();
        FlowerAdapter adapter=new FlowerAdapter(getContext(),R.layout.flowerlist,flowerList1);
        listView.setAdapter(adapter);

        //设置listview显示高度是item的和，用于解决scrollview内嵌listview只显示一个item问题
        setListViewHeightBasedOnChildren(listView);
        myViewFlipper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!AnalysisUtils.readLoginStatus(getContext()))
                    Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
                else{
                    Flower flower1=flowerList2.get(myViewFlipper.getDisplayedChild());
                    Snackbar.make(view,flower1.getText()+"", BaseTransientBottomBar.LENGTH_SHORT)
                            .setAction("加入购物车", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    addCar(flower1);
                                }
                            })
                            .show();
                }


            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(!AnalysisUtils.readLoginStatus(getContext()))
                    Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
                else{
                    Flower flower=flowerList1.get(i);
                    //Toast.makeText(HomeActivity.this, flower.getName(), Toast.LENGTH_SHORT).show();

                    Snackbar.make(view,flower.getText()+"", BaseTransientBottomBar.LENGTH_SHORT)
                            .setAction("加入购物车", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    addCar(flower);
                                }
                            })
                            .show();
                }

            }
        });



        return root;
    }
    private ImageView getImageView(int imageId) {
        ImageView myImageView = new ImageView(getContext());
        // myImageView.setImageResource(imageId);
        //myImageView.setBackgroundResource(imageId);
        Log.d("home", "getImageView:1 "+getContext().getExternalCacheDir()+"/flower"+imageId+".jpg");
        FileInputStream fileInputStream=null;
        try{
            fileInputStream = new FileInputStream(getContext().getExternalCacheDir()+"/flower"+imageId+".jpg");

            Log.d("home", "getImageView:2 ");
        }
        catch (Exception e){
            e.printStackTrace();
            Log.d("home", "getImageView:3 ");
        }
        myImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
        myImageView.setImageBitmap(bitmap);
        return myImageView;
    }
    private void initFlower(){
        Flower flower;
        //删除前三个元素
        for(int i=0;i<3;i++){
            flower=flowerList1.get(0);
            flowerList1.remove(0);
            flowerList2.add(flower);
        }
    }
    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);  // 获取item高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        // 最后再加上分割线的高度和padding高度，否则显示不完整。
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1))+listView.getPaddingTop()+listView.getPaddingBottom();
        listView.setLayoutParams(params);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

    }
    public void addCar(Flower flower){
        Order ordermap=map2.get(flower.getId());
        if(ordermap==null){
            Order neworder=new Order(flower.getId(),flower.getName(),flower.getPriece(),
                    1);
            map2.put(flower.getId(),neworder);
            DashboardViewModel.orderList.add(map2.get(flower.getId()));

        }else{
            ordermap.setFnumber(ordermap.getFnumber()+1);
        }
    }

}