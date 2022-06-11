package com.example.flower;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.example.flower.ui.notifications.NotificationsFragment;
import com.google.gson.Gson;

import java.util.Map;



public class LoginActivity extends AppCompatActivity {
    //实现记住密码功能
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private CheckBox rememberPass;

    private TextView textView1;

    private Button btn_login;
    private EditText usr,pwd;
    private String username,password;

    //此u仅作为验证密码，只有id和psw属性赋值
    private User u;
    private int logincheck=0;
    private ProgressBar progressBar;

    Map<String,String> result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //找到按钮
        btn_login = findViewById(R.id.btn_login);

        //找到editText的值
        usr = findViewById(R.id.username);
        pwd = findViewById(R.id.password);

        progressBar=findViewById(R.id.progress_bar);
        //实现点击文字功能
        textView1=(TextView)findViewById(R.id.textzhuce);
        final SpannableStringBuilder style = new SpannableStringBuilder();

        //设置文字
        style.append("没有账户？立即注册");

        //设置部分文字点击事件
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                //Toast.makeText(LoginActivity.this, "触发点击事件!", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(LoginActivity.this,registeActivity.class);
                startActivity(intent);
            }
        };
        style.setSpan(clickableSpan, 5, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView1.setText(style);

        //设置部分文字颜色
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#ff0000"));
        style.setSpan(foregroundColorSpan, 5, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //配置给TextView
        textView1.setMovementMethod(LinkMovementMethod.getInstance());
        textView1.setText(style);


        //实现记住密码功能
        pref= PreferenceManager.getDefaultSharedPreferences(this);
        rememberPass=(CheckBox) findViewById(R.id.remember_pass);
        boolean isRemember =pref.getBoolean("remember_password",false);
        if(isRemember){
            String account=pref.getString("account","");
            String password=pref.getString("password","");
            usr.setText(account);
            pwd.setText(password);
            rememberPass.setChecked(true);
        }

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = usr.getText().toString().trim();
                password = pwd.getText().toString().trim();
                u = new User();
                u.setUser_id(Integer.parseInt(username));
                u.setPassword(password);
                //实现记住密码
                editor=pref.edit();
                if(rememberPass.isChecked()){
                    editor.putBoolean("remember_password",true);
                    editor.putString("account",username);
                    editor.putString("password",password);
                }else{
                    editor.clear();
                }editor.apply();
                final Thread thread=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //此条调用密码验证程序
                         result= DBUtils.Login(LoginActivity.this,u);
                    }
                });
                thread.start();
                progressBar.setVisibility(View.VISIBLE);
                //while空语句等待线程结束
                while (thread.isAlive())
                    ;
                progressBar.setVisibility(View.GONE);
                if (result != null&& result.size() > 0) {

                    logincheck=1;
                    u=new User();
                    for (Map.Entry<String, String> entry : result.entrySet()) {
                        Log.d("aaa", "run: "+entry.getKey() + ", value = " + entry.getValue());
                        String shuxing=entry.getKey();
                        if (shuxing.equals("id")) {
                            u.setUser_id(Integer.parseInt(entry.getValue()));
                            Log.d("aaa", "Login:1");
                        } else if (shuxing.equals("psw") ) {
                            u.setPassword(entry.getValue());
                            Log.d("aaa", "Login:2");
                        } else if (shuxing.equals("tel")) {
                            u.setUsertel(entry.getValue());
                            Log.d("aaa", "Login:3");
                        } else if (shuxing.equals("realname")) {
                            u.setUserRealname(entry.getValue());
                            Log.d("aaa", "Login:4");
                        } else if (shuxing.equals("name")) {
                            u.setUsername(entry.getValue());
                            Log.d("aaa", "Login:4");
                        }
                    }
                    //test
                    Gson gson = new Gson();
                    String strJson = gson.toJson(u);
                    AnalysisUtils.putUser(LoginActivity.this,strJson);

                }else{
                    Log.d("aaa", "run: null");
                    logincheck=2;
                }

                if(logincheck==2){
                    AlertDialog.Builder dialog=new AlertDialog.Builder(LoginActivity.this);
                    dialog.setTitle("提示");
                    dialog.setMessage("账户或密码错误。");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    dialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    dialog.show();
                }else{
                    //Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                   // startActivity(intent);
                    onBackPressed();
                    finish();
                }





            }
        });



    }
}



