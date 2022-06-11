package com.example.flower;

import static android.media.ThumbnailUtils.extractThumbnail;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PhotoActivity extends AppCompatActivity {
    public static final int TAKE_PHOTO=1;
    public static final int CHOOSE_PHOTO=2;
    private ImageView picture;
    private Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        //获取user信息
        String user=AnalysisUtils.getUser(PhotoActivity.this);
        Gson a=new Gson();
        User use=a.fromJson(user,User.class);


        //按钮
        Button takePhoto=(Button) findViewById(R.id.imagebutton);
        Button choosePhoto=(Button)findViewById(R.id.choose_album);
        Button photoButton=(Button)findViewById(R.id.photobutton);
        //隐藏按钮
        photoButton.setVisibility(View.INVISIBLE);
        picture=(ImageView) findViewById(R.id.imag_myphoto);
        if(!AnalysisUtils.readLoginStatus(PhotoActivity.this)){
            Toast.makeText(PhotoActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
        //判断该用户是否有头像文件，有 显示，没有 不显示
        File myPhoto=new File(getExternalCacheDir(),"user"+use.getUser_id()+".jpg");
        if(myPhoto.exists()){
            FileInputStream fileInputStream=null;
            try{
                fileInputStream = new FileInputStream(myPhoto);
            }
            catch (Exception e){
                e.printStackTrace();
                Log.d("home", "getImageView:3 ");
            }
            Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
            picture.setImageBitmap(bitmap);
        }else{
            ;
        }
        //拍照按钮
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("aaaf", "onActivityResult: 创建照片路径");
                //照片存在file里面
                File outputImage=new File(getExternalCacheDir(),"myphoto.jpg");
                try{
                    if(outputImage.exists()){
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                    Log.d("aaaf", "onActivityResult: 创建成功");
                }catch (IOException e){
                    e.printStackTrace();
                    Log.d("aaaf", "onClick: "+e.toString());
                }
                if(Build.VERSION.SDK_INT>=24){
                    imageUri= FileProvider.getUriForFile(PhotoActivity.this,
                            "com.example.flower.fileprovider",outputImage);
                }else{
                    imageUri= Uri.fromFile(outputImage);
                }
                //启动相机
                Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,TAKE_PHOTO);

            }
        });
        //选照片按钮
        choosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("aaaf", "onActivityResult: 选择照片");
                if(ContextCompat.checkSelfPermission(PhotoActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                        PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(PhotoActivity.this,new
                            String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else{
                    openAlbum();
                }
            }
        });
        //保存按钮
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                FileInputStream input=null;
                FileOutputStream output=null;
                try {
                    input = new FileInputStream(getExternalCacheDir()+"/myphoto.jpg");
                    output = new FileOutputStream(getExternalCacheDir()+"/user"+use.getUser_id()+".jpg");
                    byte[] buf = new byte[1024];
                    int bytesRead=-1;
                    while((bytesRead=input.read(buf))!=-1){
                        output.write(buf,0,bytesRead);
                    }
                    input.close();
                    output.close();
                }catch (Exception e){
                    e.printStackTrace();
                }

                 */
                Bitmap bitmap=BitmapFactory.decodeFile(getExternalCacheDir()+"/myphoto.jpg");
                //自带压缩图片算法
                Bitmap nbitmap=extractThumbnail(bitmap,100,100 );
                File file=new File(getExternalCacheDir()+"/user"+use.getUser_id()+".jpg");//将要保存图片的路径
                try{
                   // FileOutputStream out = new FileOutputStream(file);
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                   nbitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                   bos.flush();
                    bos.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
                putPhotosql(PhotoActivity.this,use.getUser_id(),file);
                photoButton.setVisibility(View.INVISIBLE);

            }
        });
    }
    private void putPhotosql(Context context,int userid,File path){

        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    DBUtils.putBitmap(context,userid,path);
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        });
        thread.start();
    }
    private void openAlbum(){
        Intent intent=new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        //打开相册
        startActivityForResult(intent,CHOOSE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Log.d("aaaf", " 得权限");
                    openAlbum();
                }else{
                    Toast.makeText(this, "你没有授予权限", Toast.LENGTH_SHORT).show();
                }break;
            default:
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        Log.d("aaaf", "onActivityResult: 回来了");
        switch (requestCode){
            case TAKE_PHOTO:
                Log.d("aaaf", "onActivityResult: 显示照片1");
                if(resultCode==RESULT_OK){

                    try{
                        //显示照片
                        Bitmap bitmap= BitmapFactory.decodeStream
                                (getContentResolver().openInputStream(imageUri));
                        picture.setImageBitmap(bitmap);
                        aaa();
                    }catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Log.d("aaaf", "onActivityResult: "+e.toString());
                    }
                }
                break;
            case CHOOSE_PHOTO:
                Log.d("aaaf", "onActivityResult: 显示照片2");
                if(resultCode==RESULT_OK){
                    //不同安卓版本，不同的处理
                    if(Build.VERSION.SDK_INT>=19){
                        handleImageOnKitKat(data);
                    }else{
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }
    private void aaa(){
        Button photoButton=(Button)findViewById(R.id.photobutton);
        photoButton.setVisibility(View.VISIBLE);;
    }

    private void handleImageOnKitKat(Intent data){
        Log.d("aaaf", "onActivityResult: 显示照片2.1");
        String imagePath=null;
        Uri uri=data.getData();
        if(DocumentsContract.isDocumentUri(this,uri)){
            //如果是document类型uri
            String docId=DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath); // 根据图片路径显示图片
    }
    private void handleImageBeforeKitKat(Intent data) {
        Log.d("aaaf", "onActivityResult: 显示照片2.2");
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }
    @SuppressLint("Range")
    private String getImagePath(Uri uri, String selection) {
        Log.d("aaaf", "onActivityResult: 显示照片2.12.1");
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        Log.d("aaaf", "onActivityResult: 显示照片2.n");
        if (imagePath != null) {
                    //显示照片
                    Bitmap bitmap= BitmapFactory.decodeFile
                            (imagePath);
                    picture.setImageBitmap(bitmap);
                //将照片存在目录下
                //创建文件
                File outputImage=new File(getExternalCacheDir(),"myphoto.jpg");
                FileInputStream input=null;
                FileOutputStream output=null;
                try {
                    input = new FileInputStream(imagePath);
                    output = new FileOutputStream(outputImage);
                    byte[] buf = new byte[1024];
                    int bytesRead=-1;
                    while((bytesRead=input.read(buf))!=-1){
                        output.write(buf,0,bytesRead);
                    }
                    input.close();
                    output.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
                aaa();


            // Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            Log.d("aaaf", "onActivityResult: 显示照片2.n.1"+imagePath);
            //picture.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }
}