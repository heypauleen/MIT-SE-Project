package com.example.thriftawayui;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeView> {

    List<Items> itemList;
    List<String> imageList;
    Context context;

    public HomeAdapter(Context context, List<Items> items, List<String> image ) {
        this.itemList = items;
        this.imageList = image;
        this.context = context;

    }

    @NonNull
    @Override
    public HomeView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

       View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_home, viewGroup, false);
        return new HomeView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final HomeView homeView, final int position) {
        //homeView.img_home.setImageResource(imageList.get(position));

        final String key = itemList.get(position).getItemid();
        homeView.txt_title.setText(itemList.get(position).getItemname());
        homeView.txt_details.setText(itemList.get(position).getItemdesc());
        if(imageList.get(position).equals("null")){

        } else{

        }
        Glide.with(context).load(imageList.get(position)).into(homeView.img_home);


        homeView.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//
                Bundle bundle = new Bundle();
                bundle.putString("itemid", key);

                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                SelectedItemActivity myFragment = new SelectedItemActivity();
                myFragment.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, myFragment).addToBackStack(null).commit();

            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class HomeView extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView img_home;
        TextView txt_title, txt_details;
        public HomeView(@NonNull View itemView) {
            super(itemView);

            img_home = (ImageView)itemView.findViewById(R.id.imghome);
            txt_title = (TextView)itemView.findViewById(R.id.txttitle);
            txt_details = (TextView)itemView.findViewById(R.id.txtdetails);
        }

        @Override
        public void onClick(View view) {


        }
    }

}
