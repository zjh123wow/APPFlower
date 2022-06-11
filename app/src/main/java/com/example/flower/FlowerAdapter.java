package com.example.flower;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.FileInputStream;
import java.util.List;

public class FlowerAdapter extends ArrayAdapter<Flower> {
    private int resourseId;
    public FlowerAdapter(Context context, int textViewResourceId, List<Flower> objects){
        super(context,textViewResourceId,objects);
        resourseId=textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        Flower flower=getItem(position);
        View view= LayoutInflater.from(getContext()).inflate(resourseId,parent,false);
        ImageView flowerImage=(ImageView)view.findViewById(R.id.img);
        TextView flowerName=(TextView) view.findViewById(R.id.title);
        TextView flowerInfo=(TextView)view.findViewById(R.id.info);
        TextView flowerPri=(TextView)view.findViewById(R.id.jiage);
       // flowerImage.setImageResource(flower.getId());
        FileInputStream fileInputStream=null;
        try{
            fileInputStream = new FileInputStream(getContext().getExternalCacheDir()+"/flower"+flower.getId()+".jpg");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
        flowerImage.setImageBitmap(bitmap);
        flowerName.setText(flower.getName());
        flowerInfo.setText(flower.getText());
        flowerPri.setText(Float.toString(flower.getPriece())+"元");
        return view;
    }

}

