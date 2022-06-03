package com.example.thriftawayui;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

     RecyclerView recycleHome;
    List<Items> itemslist;
    List<String> images;

    DatabaseReference databaseReference;
    RecyclerView.Adapter adapter;
    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false );
        recycleHome = view.findViewById(R.id.recyclerhome);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recycleHome.setLayoutManager(gridLayoutManager);

        databaseReference = FirebaseDatabase.getInstance().getReference("Items");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemslist = new ArrayList<>();
                images = new ArrayList<>();

                for(DataSnapshot postSnapchat : dataSnapshot.getChildren()){
                    Items item = postSnapchat.getValue(Items.class);
                    itemslist.add(item);
                    Log.d("TAG", ""+postSnapchat.child("images").getChildrenCount());
                   if(postSnapchat.child("images").getChildrenCount() != 0 ){
                        images.add(postSnapchat.child("images").child("image1").getValue().toString());
                    }

                    //Log.d("TAG",postSnapchat.child("images").child("image1").getValue().toString());
                    /*for(DataSnapshot imageSnapchat : postSnapchat.child("images").getChildren()){
                        Images image = postSnapchat.getValue(Images.class);
                    }*/
                }
                adapter = new HomeAdapter(getContext(),itemslist,images);
                recycleHome.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*imageList.add(R.mipmap.gopro);
        imageList.add(R.mipmap.applewatch);
        imageList.add(R.mipmap.friends);
        imageList.add(R.mipmap.harrypotter);
        imageList.add(R.mipmap.iphone11promax);
        imageList.add(R.mipmap.ratanbag);

        titleList.add("Go Pro Hero 5");
        titleList.add("Apple Watch");
        titleList.add("Friends Shirt");
        titleList.add("Harry Potter Set");
        titleList.add("iPhone 11 Pro Max");
        titleList.add("Ratan Bag");

        detailsList.add("Details");
        detailsList.add("Details");
        detailsList.add("Details");
        detailsList.add("Details");
        detailsList.add("Details");
        detailsList.add("Details");*/


        return view;


    }
}
