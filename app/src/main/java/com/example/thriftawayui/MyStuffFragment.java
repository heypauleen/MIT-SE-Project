package com.example.thriftawayui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyStuffFragment extends Fragment {
    @Nullable
    DatabaseReference databaseReference;
    RecyclerView recycleHome;
    List<String> images;
    List<Items> itemslist;
    FirebaseUser user;
    String userid;
    RecyclerView.Adapter adapter;
    FloatingActionButton btn_additem;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_mystuff, container, false );
        recycleHome = view.findViewById(R.id.recyclermystuff);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recycleHome.setLayoutManager(gridLayoutManager);
        btn_additem = view.findViewById(R.id.btnadditem);
        user = FirebaseAuth.getInstance().getCurrentUser();
        userid = user.getUid();


        databaseReference = FirebaseDatabase.getInstance().getReference("Items");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemslist = new ArrayList<>();
                images = new ArrayList<>();

                for(DataSnapshot postSnapchat : dataSnapshot.getChildren()){
                    Items item = postSnapchat.getValue(Items.class);

                    if (item.getUploadedby().equals(userid)){
                        itemslist.add(item);
                        if(postSnapchat.child("images").getChildrenCount() != 0 ){
                            images.add(postSnapchat.child("images").child("image1").getValue().toString());
                        }
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


        btn_additem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment newFragment = new AddItemFragment();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, newFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return view;


    }
}
