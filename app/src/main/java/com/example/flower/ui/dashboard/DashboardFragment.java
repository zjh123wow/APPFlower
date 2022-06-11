package com.example.flower.ui.dashboard;

import static com.example.flower.ui.dashboard.DashboardViewModel.map2;
import static com.example.flower.ui.dashboard.DashboardViewModel.orderList;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.flower.DBUtils;
import com.example.flower.Flower;
import com.example.flower.LoginActivity;
import com.example.flower.Order;
import com.example.flower.OrderAdapter;
import com.example.flower.R;
import com.example.flower.databinding.FragmentDashboardBinding;
import com.example.flower.ui.home.HomeFragment;

import java.util.List;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private FragmentDashboardBinding binding;
    public static List<Flower> flowerList;
    public static TextView textView;
    public static CheckBox checkBox;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        flowerList= DBUtils.getFlowerlistFromfile(getContext());

        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        dashboardViewModel.map= HomeFragment.map;
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
          textView = binding.textTotmoney;
        setUI();
         checkBox=binding.checkboxAll;
       setCHECKall();
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkBox.isChecked()){
                    for(int i=0;i<orderList.size();i++){
                        Order oder1=orderList.get(i);
                        oder1.setSelect(true);
                    }
                }else{
                    for(int i=0;i<orderList.size();i++){
                        Order oder1=orderList.get(i);
                        oder1.setSelect(false);
                    }
                }
                setUI();
            }
        });
        Button givembutton=binding.buttonGivemoney;
        givembutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog=new AlertDialog.Builder(getContext());
                dialog.setTitle("提示");
                dialog.setMessage("假装这是支付界面。");
                dialog.setCancelable(false);
                dialog.setPositiveButton("确定支付", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for(int j=0;j<orderList.size();j++){
                            Order oder1=orderList.get(j);
                            if(oder1.getSelect()){
                                //执行插数据库操作

                                //执行完删除
                                map2.remove(oder1.getFid());
                                orderList.remove(j);
                                j--;//回退一位
                            }
                        }
                        setUI();
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog.show();


            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    public  void setUI(){
        OrderAdapter adapter=new OrderAdapter(getContext(),R.layout.orderslist,orderList);
        ListView listVieworder=binding.listviewOrder;
        listVieworder.setAdapter(adapter);
        setTextView();

    }
    public static void setCHECKall(){
        for(int i=0;i<orderList.size();i++){
            Order oder1=orderList.get(i);
            if(!oder1.getSelect()){
                checkBox.setChecked(false);
                return;
            }
        }
        checkBox.setChecked(true);
    }
    public static void setTextView(){
        textView.setText("合计:￥"+DashboardViewModel.getTotmonry(orderList));
    }
}