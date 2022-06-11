package com.example.flower.ui.notifications;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.flower.AddressActivity;
import com.example.flower.AnalysisUtils;
import com.example.flower.LoginActivity;
import com.example.flower.MainActivity;
import com.example.flower.PhotoActivity;
import com.example.flower.R;
import com.example.flower.User;
import com.example.flower.databinding.FragmentNotificationsBinding;
import com.example.flower.ui.dashboard.DashboardViewModel;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private FragmentNotificationsBinding binding;
    public  static User user;
    public static ImageView imageView;
    public static TextView textView2;
    public static TextView textView1;
    public static String u;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        textView2=binding.textPhoto;
         textView1=binding.textId;
         imageView=binding.imagePhoto;
        ConstraintLayout constraintLayout=binding.constraintLayout1;
        Log.d("noti", "onCreateView: "+u);
        ConstraintLayout constraintLayout2=binding.constraintLayout2;

        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getContext(), "整个布局被点击", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(getContext(), PhotoActivity.class);
                startActivityForResult(intent,0);
            }
        });
        constraintLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(),AddressActivity.class);
                startActivity(intent);
            }
        });
        //判断是否登录
        if (AnalysisUtils.readLoginStatus(getContext())){
           newphotoview(getContext());

        }else{
            //清空头像
            imageView.setImageResource(R.drawable.nophoto);
            //跳转登录页面
            Intent intent=new Intent(getActivity(), LoginActivity.class);
            startActivityForResult(intent,0);
        }
        Button button1=binding.buttonResetuser;
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //清空头像
                imageView.setImageResource(R.drawable.nophoto);
                textView2.setText("未登录");
                textView1.setText("未登录");
                AnalysisUtils.cleanLoginStatus(getContext());
                DashboardViewModel.orderList=new ArrayList<>();
                DashboardViewModel.map2=new HashMap<>();
                getActivity().onBackPressed();

            }
        });

        return root;
    }
    public static void newphotoview(Context context) {
        if(!AnalysisUtils.readLoginStatus(context)){
            //清空头像
            imageView.setImageResource(R.drawable.nophoto);
        }else{
            u=AnalysisUtils.getUser(context);
            Log.d("aaaflogin", "newphotoview: "+u);
            if(u!=null&&u!=""){
                user=new Gson().fromJson(u,User.class);
                Log.d("aaaflogin", "newphotoview: "+user.toString());
                textView2.setText(user.getUsername());
                textView1.setText("id:"+user.getUser_id());
            }
            //判断该用户是否有头像文件，有 显示，没有 不显示
            File myPhoto = new File(context.getExternalCacheDir(), "user" + user.getUser_id() + ".jpg");
            if (myPhoto.exists()) {
                FileInputStream fileInputStream = null;
                try {
                    fileInputStream = new FileInputStream(context.getExternalCacheDir() + "/user" + user.getUser_id() + ".jpg");

                    Log.d("home", "getImageView:2 ");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("home", "getImageView:3 ");
                }
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
                imageView.setImageBitmap(bitmap);

            }else{
                //清空头像
                imageView.setImageResource(R.drawable.nophoto);
            }
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        newphotoview(getContext());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}