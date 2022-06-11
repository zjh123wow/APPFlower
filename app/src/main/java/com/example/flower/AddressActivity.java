package com.example.flower;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

public class AddressActivity extends AppCompatActivity {

    //获取user信息
    String user;

    Address address;
    boolean flag=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        user=AnalysisUtils.getUser(AddressActivity.this);
        Gson a=new Gson();
        User use=a.fromJson(user,User.class);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        //初始化三个输入框
        EditText editText1=(EditText)findViewById(R.id.addre_edtext1);
        EditText editText2=(EditText)findViewById(R.id.addre_edtext2);
        EditText editText3=(EditText)findViewById(R.id.addre_edtext3);

        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                address=DBUtils.getAddres(use.getUser_id());
            }
        });
        thread.start();
        while(thread.isAlive()) ;

        if(address==null){
                ;
        }else{
            editText1.setText(address.getName());
            editText2.setText(address.getTel());
            editText3.setText(address.getAddressdesc());
        }

        //button的点解事件
        Button button1=(Button) findViewById(R.id.button_address);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //判断输入数据是否满足格式要求
                if(TextUtils.isEmpty(editText1.getText().toString())||TextUtils.isEmpty(editText2.getText().toString())
                        ||TextUtils.isEmpty(editText3.getText().toString()))
                {
                    Toast.makeText(AddressActivity.this, "信息未填完整", Toast.LENGTH_SHORT).show();
                }
                else{
                    Address newaddress=new Address(editText1.getText().toString().trim(),editText2.getText().toString().trim(),
                            editText3.getText().toString().trim());

                    Thread thread=new Thread(new Runnable() {
                        @Override
                        public void run() {
                            flag=DBUtils.putAddres(newaddress, use.getUser_id());
                        }
                    });
                    thread.start();
                    while(thread.isAlive()) ;
                    if(flag){
                        Toast.makeText(AddressActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}