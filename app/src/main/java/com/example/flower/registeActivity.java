package com.example.flower;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class registeActivity extends AppCompatActivity {
    private EditText edT1,edT2,edT3,edT4,edT5;
    private User user;
    private Button button2;
    private boolean a;
    private int[] last_insert=new int[1];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registe);

        edT1=(EditText) findViewById(R.id.edit_text1);
        edT2=(EditText) findViewById(R.id.edit_text2);
        edT3=(EditText) findViewById(R.id.edit_text3);
        edT4=(EditText) findViewById(R.id.edit_text4);
        edT5=(EditText) findViewById(R.id.edit_text5);

        button2=(Button)findViewById(R.id.button_1);


        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //判断输入数据是否满足格式要求
                if(TextUtils.isEmpty(edT1.getText().toString())||TextUtils.isEmpty(edT2.getText().toString())
                        ||TextUtils.isEmpty(edT3.getText().toString())||TextUtils.isEmpty(edT4.getText().toString())||
                        TextUtils.isEmpty(edT5.getText().toString()))
                {
                    Toast.makeText(registeActivity.this, "信息未填完整", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(isMobileNO(edT3.getText().toString())){
                        //手机号验证成功
                        if(isPasswordNO(edT4.getText().toString(),edT5.getText().toString())){
                            //

                            //开始插入数据库
                            user=new User(edT1.getText().toString().trim(),edT4.getText().toString().trim(),
                                    edT3.getText().toString().trim(),edT2.getText().toString().trim());
                            final Thread thread=new Thread(new Runnable() {
                                @Override
                                public void run() { a=DBUtils.Registe(user,last_insert);
                                }
                            });
                            thread.start();
                            //while空语句等待线程结束
                            while (thread.isAlive());

                            //插入成功显示信息
                            if(a){
                                AlertDialog.Builder dialog=new AlertDialog.Builder(registeActivity.this);
                                dialog.setTitle("提示");
                                dialog.setMessage("恭喜你注册成功。id为"+last_insert[0]);
                                dialog.setCancelable(false);
                                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent=new Intent(registeActivity.this,LoginActivity.class);
                                        startActivity(intent);
                                        finish();

                                    }
                                });
                                dialog.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent=new Intent(registeActivity.this,LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                dialog.show();
                            }else{
                                AlertDialog.Builder dialog=new AlertDialog.Builder(registeActivity.this);
                                dialog.setTitle("提示");
                                dialog.setMessage("因系统原因注册失败\n，请您退出软件,八成是废了");
                                dialog.setCancelable(false);
                                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogInterface, int i) { }
                                });
                                dialog.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) { }
                                });
                                dialog.show();
                            }

                            //
                        }else{
                            Toast.makeText(registeActivity.this, "密码格式错误或两次密码不一致", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(registeActivity.this, "手机号码格式错误", Toast.LENGTH_SHORT).show();
                    }
                }



            }
        });


    }
    public static boolean isMobileNO(String mobiles) {

        String telRegex = "[1][358]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        return mobiles.matches(telRegex);
    }
    boolean isPasswordNO(String password1,String password2){
        String pasRegex="^\\d{6,10}$";
        if(password1.equals(password2)){
            return password2.matches(pasRegex);
        }else{
            return false;
        }
    }
}