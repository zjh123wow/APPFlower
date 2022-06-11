package com.example.flower;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
public class DBUtils {
    private  static User u;
    private static String driver = "com.mysql.jdbc.Driver";//MySQL 驱动
    //下面填连接数据库ip和端口
    private static String url = "127.0.0.1:3306" +
            "/flower?useUnicode=true&characterEncoding=utf-8&useSSL=false";//MYSQL数据库连接Url

    private static String user = "flower";//用户名
    private static String password = "xianhua@1";//密码
    private static final String TAG = "DBU";
    public static Connection getConn(){
        Connection connection = null;
        try{
            Class.forName(driver);
            Log.i(TAG, "驱动加载成功" );
        }catch (Exception e){
            Log.i(TAG,"驱动加载失败\n"+e.toString() );
            return null;
        }
        try{
            Log.i(TAG,"连接");
            Log.i(TAG, "驱动加载成功 ");
            // url的ip写成本机地址，不能写成localhost，同时手机和电脑连接的网络必须是同一个
            // 尝试建立到给定数据库URL的连接
            connection = DriverManager.getConnection(url,user,password);

        }catch (Exception e){
            Log.i(TAG, "Mysql连接失败"+e.toString());
            e.printStackTrace();
        }

        return connection;
    }

    //登录
    public static HashMap<String, String>  Login(Context context,User user) {
        HashMap<String, String> map = new HashMap<>();
        Connection conn = getConn();
        try {
            String sql= "select id,name,psw,qq,tel,realname from user where id =? and psw =?";
            PreparedStatement ps=conn.prepareStatement(sql);
            ps.setInt(1,user.getUser_id());
            ps.setString(2,user.getPassword());
            ResultSet res=ps.executeQuery();
            Log.d("aaa", "Login:");
            if(!res.next())
            {
                res.previous();
                conn.close();
                ps.close();
                res.close();
                return null;
            } else {
                //res.next会后移一位，这里移回去
                res.previous();

                int cnt = res.getMetaData().getColumnCount();
                res.next();
                for (int i = 1; i <= cnt; ++i) {
                    String field = res.getMetaData().getColumnName(i);
                    if(field.equals("photo")) continue;
                    map.put(field, res.getString(field));
                    Log.d("aaa", "Login:null "+field+res.getString(field));
                }
                conn.close();
                ps.close();
                res.close();
                Log.d("aaa", "Login:map ");
                readBlob(context, user.getUser_id());
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("aaa", "Login: DBU");
            return null;
        }
    }
    public static Boolean Registe(User user,int[] lastinsert) {
        Connection conn = getConn();
        Log.d("DBUtil", "Login:aaa ");
        try {
            Log.d("aaa", "Registe: "+user.getUserRealname());
            String sql= "insert into `user`(name,psw,tel,realname) VALUES ('"+user.getUsername()+
                    "','"+user.getPassword()+"','"+user.getUsertel()+"','"+user.getUserRealname()+"');";

            Statement ps = conn.createStatement();
            // ps.execute();
            ps.executeUpdate(sql);
            sql="select last_insert_id()";
            ResultSet res = ps.executeQuery(sql);
            //存刚才插入的id
            res.next();
            Log.d("aaainsertid", "Registe: "+res+res.getInt(1));
            lastinsert[0]=res.getInt(1);
            // AnalysisUtils.putLastinsert(,);
            conn.close();
            Log.d("aaa", "Login:victory ");
            return true;

        }
        catch (Exception e) {
            e.printStackTrace();
            Log.d("aaa", "Login:false"+e.toString());
            return false;
        }
    }

    public static void readBlob(Context context,int userid) throws Exception {

        Connection connection=getConn();
        // sql
        String sql = "select * from user where id='"+userid+"'";
        // 读取不接受参数可以直接用statment
        Statement st = connection.createStatement();
        // 执行查询
        ResultSet rs = st.executeQuery(sql);
        // 确定只有一行
        if (rs.next()) {
            int id = rs.getInt("id");
            // 获取图片
            InputStream in = rs.getBinaryStream("photo");// 数据库字段
            if(in==null) return;
            // 声明out
            File outputImage=new File(context.getExternalCacheDir(),"user"+userid+".jpg");
            OutputStream out = new FileOutputStream(outputImage);
            byte[] b = new byte[1024];
            int len = 0;
            while ((len = in.read(b)) != -1) {
                out.write(b, 0, len);
            }
            in.close();
            out.close();
            connection.close();
        }
    }
    public static void putBitmap(Context context,int userid,File path) throws Exception{
        Connection connection=getConn();
        InputStream in = new FileInputStream(path);
        // OutputStream out=new FileOutputStream(path);
        String sql = "update user set photo = ? where id=?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setBlob(1, in);
        pstmt.setInt(2,userid);
        //执行
        pstmt.executeUpdate();
        //st.close();
        connection.close();
    }


    public static List<Flower> initflower(Context context){
        List<Flower> flowerList=new ArrayList<>();
        File myFlowerlist=new File(context.getExternalCacheDir(),"flowerlist.text");
       if(isupdate(context)&&myFlowerlist.exists()){
           return getFlowerlistFromfile(context);

       }else{
           Connection connection=getConn();
           String sql = "select * from flower";
           try{
               PreparedStatement pst=connection.prepareStatement(sql);
               // 执行查询
               ResultSet rs = pst.executeQuery();
               Flower flower;
               Log.d("aaaff", "initflowersele: ");
               while (rs.next()) {
                   int id = rs.getInt("id");
                   flower=new Flower(rs.getInt("id"),rs.getString("name"),rs.getString("flowdesc"),rs.getFloat("price"));
                   flowerList.add(flower);
                   // 获取图片
                   InputStream in = rs.getBinaryStream("photo");// 数据库字段
                   // 声明out
                   File outputImage=new File(context.getExternalCacheDir(),"flower"+id+".jpg");
                   OutputStream out = new FileOutputStream(outputImage);
                   byte[] b = new byte[1024];
                   int len = 0;
                   while ((len = in.read(b)) != -1) {
                       out.write(b, 0, len);
                   }
               }
               rs.close();pst.close();
               connection.close();
               Log.d("aaaff", "initflower存完: ");
           }catch (Exception e){
               e.printStackTrace();
               Log.d("aaaff", "initflower: "+e.toString());
           }
           try {
               String s = new Gson().toJson(flowerList);
               File outputlist = new File(context.getExternalCacheDir(), "flowerlist.text");
               FileOutputStream out = new FileOutputStream(outputlist);
               byte[]b=s.getBytes();
               for(int i=0; i<b.length; i++) {
                   out.write(b[i]);
               }
               out.close();
           }
           catch (Exception e){
               e.printStackTrace();
           }

           return flowerList;
       }
    }
    //判断flower数据库是否更新
    public static boolean isupdate(Context context){
        Connection connection=getConn();
        String sql = "SELECT update_time  FROM information_schema.tables " +
                "WHERE table_schema = 'flower' AND table_name = 'flower' ;" ;
        boolean flag=false;
        try{
            PreparedStatement pst=connection.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            rs.next();
            Timestamp s=rs.getTimestamp(1);
            String lastflower=AnalysisUtils.getFlowerTimestamp(context);
            if(lastflower.equals(s.toString()))
                flag=true;
            else AnalysisUtils.putFlowerTimestamp(context,s);
            rs.close();pst.close();
            connection.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }
    public static List<Flower> getFlowerlistFromfile(Context context){
        List<Flower> flowerList=new ArrayList<>();
        String str="";
        try{
            File file = new File(context.getExternalCacheDir(), "flowerlist.text");//定义一个file对象，用来初始化FileReade
            FileReader reader = new FileReader(file);//定义一个fileReader对象，用来初始化BufferedReader
            BufferedReader bReader = new BufferedReader(reader);//new一个BufferedReader对象，将文件内容读取到缓存
            StringBuilder sb = new StringBuilder();//定义一个字符串缓存，将字符串存放缓存中
            String s = "";
            while ((s =bReader.readLine()) != null) {//逐行读取文件内容，不读取换行符和末尾的空格
                sb.append(s + "\n");//将读取的字符串添加换行符后累加存放在缓存中
            }
            bReader.close();
            str = sb.toString();
            Log.d(TAG, "initflower: "+str);
        }catch (Exception e){
            e.printStackTrace();
        }
        //将list集合反序列化
        Type type =new TypeToken<List<Flower>>(){
        }.getType();
        flowerList = new Gson().fromJson(str,type);
        return flowerList;
    }
    public static Address getAddres(int i){
        Address address=null;
        Connection connection=getConn();
        // sql
        String sql = "select * from address where id=? ";
        try{
            PreparedStatement pst=connection.prepareStatement(sql);
            pst.setInt(1,i);
            ResultSet rs=pst.executeQuery();
            if(rs.next()){
                address=new Address(rs.getString("name"),rs.getString("tel"),rs.getString("address"));
            }else{
                return null;
            }
            rs.close();
            pst.close();
            connection.close();
        }catch(Exception e){
            e.printStackTrace();
        }


        return address;
    }
    public static boolean putAddres(Address a,int i){
        Connection connection=getConn();
        String sql;
        if(getAddres(i)==null){
            sql = "insert into `address`(id,name,tel,address) VALUES ('"+i+"',?,?,?);";
        }else{
            sql="update address set name=?,tel=?,address=? where id='"+i+"'";
        }
        try{
            PreparedStatement pst=connection.prepareStatement(sql);
            pst.setString(1,a.getName());
            pst.setString(2,a.getTel());
            pst.setString(3,a.getAddressdesc());
            pst.executeUpdate();
            pst.close();
            connection.close();
            return true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

}
