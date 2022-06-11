package com.example.flower;

import static com.example.flower.ui.dashboard.DashboardViewModel.map2;
import static com.example.flower.ui.dashboard.DashboardViewModel.orderList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.flower.ui.dashboard.DashboardFragment;
import com.example.flower.ui.dashboard.DashboardViewModel;

import java.io.FileInputStream;
import java.util.Iterator;
import java.util.List;

public class OrderAdapter extends ArrayAdapter<Order> {
    private int resourceId;
    public  OrderAdapter(Context context,int textViewResourceId,
                         List<Order> objects){
        super(context,textViewResourceId,objects);
        resourceId=textViewResourceId;
    }
    //通过这个判断全选按钮是否应该选中
    public static boolean oDeradaflag=true;

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //return super.getView(position, convertView, parent);
        Order order=getItem(position);
        View view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        ImageView OrderImage=(ImageView)view.findViewById(R.id.image_order);
        TextView OrderName=(TextView) view.findViewById(R.id.text_order_name);
        TextView OrderNumber=(TextView)view.findViewById(R.id.text_order_number);
        TextView OrderPri=(TextView)view.findViewById(R.id.text_order_price);
        Button OrderButton=(Button) view.findViewById(R.id.button_del);
        CheckBox OrderCheck=(CheckBox) view.findViewById(R.id.check_order);
        FileInputStream fileInputStream=null;
        try{
            fileInputStream = new FileInputStream(getContext().getExternalCacheDir()+"/flower"+order.getFid()+".jpg");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
        OrderImage.setImageBitmap(bitmap);
        OrderName.setText(""+order.getFname());
        OrderNumber.setText(""+order.getFnumber());
        OrderPri.setText("￥："+(order.getPriece()*order.getFnumber()));
        OrderCheck.setChecked(order.getSelect());
        if(!order.getSelect()) oDeradaflag=false;
        OrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //删除

                orderList.remove(position);
                map2.remove(order.getFid());
                notifyDataSetChanged();
                DashboardFragment.setTextView();
            }
        });
        OrderCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(OrderCheck.isChecked()){
                    order.setSelect(true);
                }else{
                    order.setSelect(false);
                    notifyDataSetChanged();
                    DashboardFragment.checkBox.setChecked(false);

                }
                DashboardFragment.setTextView();
            }
        });

        return view;
    }

}
