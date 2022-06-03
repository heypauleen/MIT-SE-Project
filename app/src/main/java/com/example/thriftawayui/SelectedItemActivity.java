package com.example.thriftawayui;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SelectedItemActivity extends Fragment {
    String key, txtname;
    TextView txt_itemname;
    ViewFlipper viewFlipper;
    List<String> imagelist;
    List<Integer> imgarray;
    Button btn_requestitem;
    ToggleButton btn_like;
    String uploadedby;
    FirebaseUser user;
    String userid;
    String itemname, itemdesc;
    DatabaseReference databaseReference;
    Requests request;
    TextView txtlikes, txt_description;
    Boolean liked;
   // DatabaseReference databaseReferenceRequests;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selecteditem, container, false );
        Bundle bundle = getArguments();
        key = bundle.getString("itemid");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Items").child(key);
        user = FirebaseAuth.getInstance().getCurrentUser();
        userid = user.getUid();
        request = new Requests();

        txtlikes = view.findViewById(R.id.txtnolikes);
        btn_like = view.findViewById(R.id.btnlike);
        txt_itemname = view.findViewById(R.id.txtitemname);
        viewFlipper = view.findViewById(R.id.viewflipimgs);
        btn_requestitem = view.findViewById(R.id.btnrequest);
        txt_description = view.findViewById(R.id.txtdescriptioncontent);

        final Likes likes = new Likes();
        final ScaleAnimation scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
        scaleAnimation.setDuration(500);
        BounceInterpolator bounceInterpolator = new BounceInterpolator();
        scaleAnimation.setInterpolator(bounceInterpolator);
        btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getActivity(), "CLICKED", Toast.LENGTH_SHORT).show();
                btn_like.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        //animation

                        compoundButton.startAnimation(scaleAnimation);

                        likes.setLikedby(userid);

                        if(isChecked){
                            databaseReference.child("likes").push().setValue(userid);

                        }else{
                            databaseReference.child("likes").child("likedby").setValue(null);

                            databaseReference.child("likes").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(DataSnapshot removeSnapshot : dataSnapshot.getChildren()){
                                        if(removeSnapshot.getValue().toString().equals(userid)){
                                            removeSnapshot.getRef().removeValue();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                    }
                });
            }
        });




        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemname = dataSnapshot.child("itemname").getValue().toString();
                itemdesc = dataSnapshot.child("itemdesc").getValue().toString();
                uploadedby = dataSnapshot.child("uploadedby").getValue().toString();

                txt_description.setText(itemdesc);
               txt_itemname.setText(itemname);
               imagelist = new ArrayList<>();
                if(dataSnapshot.hasChild("likes")){

                    for(DataSnapshot likeSnapshot : dataSnapshot.child("likes").getChildren()){
                        //Toast.makeText(getActivity(), ""+likeSnapshot.getValue(), Toast.LENGTH_SHORT).show();

                        if(likeSnapshot.getValue().equals(userid)){
                            btn_like.setChecked(true);
                            liked = true;

                        }
                    }
                    txtlikes.setText(dataSnapshot.child("likes").getChildrenCount()+" likes");

                }else{
                    txtlikes.setText("0 likes");
                }



                for(DataSnapshot postSnapchat : dataSnapshot.child("images").getChildren()){
                    String image = postSnapchat.getValue().toString();
                    imagelist.add(image);
                }
                for (int i = 0; i < imagelist.size(); i++) {
                    String downloadImageUrl = imagelist.get(i);
                    ImageView imageView = new ImageView(getContext());
                    Glide.with(getContext()).load(downloadImageUrl).into(imageView);

                    viewFlipper.addView(imageView);

                    viewFlipper.setFlipInterval(2500);
                    viewFlipper.setAutoStart(true);

                    viewFlipper.startFlipping();
                    viewFlipper.setInAnimation(getContext(), android.R.anim.slide_in_left);
                    viewFlipper.setOutAnimation(getContext(), android.R.anim.slide_out_right);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btn_requestitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userid.equals(uploadedby)){
                    new AlertDialog.Builder(getActivity()).setTitle("Error").setMessage("You can not request your own item.")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .setIcon(R.drawable.ic_error_black_24dp).show();
                } else{

                    requestItem();
                }

            }
        });

        return view;
    }
    public void requestItem(){
        new AlertDialog.Builder(getActivity())
                .setTitle("Request")
                .setMessage("Are you sure you want to request the item "+itemname+"?")

                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                            request.setUserID(userid);
                            databaseReference.child("requested by").push().setValue(request).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    new AlertDialog.Builder(getActivity()).setTitle("Success").setMessage("Item successfully requested! ")
                                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    //requested by
                                                    dialogInterface.dismiss();
                                                }
                                            })
                                            .setIcon(R.drawable.ic_check_circle).show();
                                }
                            });



                        }

                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })

                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
