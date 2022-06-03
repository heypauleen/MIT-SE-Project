package com.example.thriftawayui;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firestore.admin.v1beta1.Progress;

import static android.app.Activity.RESULT_OK;

public class EditProfileFragment extends Fragment {

    TextView txt_fullname, txt_username, txt_email, txt_contactno, txt_shortbio, txt_gender, txt_city, txt_interests;
    String fullname, username, email, contactno, shortbio, gender, city, interests;
    StorageReference storageReference;
    Button save_profile, cancel_profile;
    DatabaseReference databaseReferenceUsers;
    Users users;
    FirebaseUser user;
    String userid;
    ImageView profpic;
    Uri imgprofuri;
    String useremail;
    ProgressBar progressbar;
    public int requestcode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_editprofile, container, false );

        user = FirebaseAuth.getInstance().getCurrentUser();
        userid = user.getUid();
        useremail = user.getEmail();

        storageReference =  FirebaseStorage.getInstance().getReference("Users");
        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference("Users").child("userid");

        txt_email = view.findViewById(R.id.txtemail);
        txt_fullname = view.findViewById(R.id.txtfullname);
        txt_username = view.findViewById(R.id.txtusername);
        txt_contactno = view.findViewById(R.id.txtcontactno);
        txt_shortbio = view.findViewById(R.id.txtshortbio);
        txt_gender = view.findViewById(R.id.txtgender);
        txt_city = view.findViewById(R.id.txtcity);
        txt_interests = view.findViewById(R.id.txtinterests);
        progressbar = view.findViewById(R.id.progressadduser);
        profpic = view.findViewById(R.id.imgprofile);

        txt_email.setText(useremail);
        txt_city.setText("Zamboanga City");
        txt_city.setEnabled(false);
        txt_email.setEnabled(false);

        fullname = txt_fullname.getText().toString();
        username = txt_username.getText().toString();
        contactno = txt_contactno.getText().toString();
        shortbio = txt_shortbio.getText().toString();
        gender = txt_gender.getText().toString();
        city = txt_city.getText().toString();
        interests = txt_interests.getText().toString();

        save_profile = view.findViewById(R.id.btnsaveprofile);
        cancel_profile = view.findViewById(R.id.btncancelprofile);

        users = new Users();

        profpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestcode = 1;
                startIntent(requestcode);
            }
        });

        save_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                users.setUserid(userid);
                users.setFullname(fullname);
                users.setUsername(username);
                users.setEmail(email);
                users.setContactno(contactno);
                users.setShortbio(shortbio);
                users.setGender(gender);
                users.setCity(city);
                users.setInterests(interests);
                if(requestcode==1)
                    imageUploader(imgprofuri);
                else
                    databaseReferenceUsers.setValue(users);
            }
        });

        databaseReferenceUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    if(!dataSnapshot.child("fullname").getValue().toString().equals(null))
                        txt_fullname.setText(dataSnapshot.child("fullname").getValue().toString());
                    if(!dataSnapshot.child("username").getValue().toString().equals(null))
                        txt_username.setText(dataSnapshot.child("username").getValue().toString());
                    if(!dataSnapshot.child("email").getValue().toString().equals(null))
                        txt_email.setText(dataSnapshot.child("email").getValue().toString());
                    if(!dataSnapshot.child("contactno").getValue().toString().equals(null))
                        txt_contactno.setText(dataSnapshot.child("contactno").getValue().toString());
                    if(!dataSnapshot.child("shortbio").getValue().toString().equals(null))
                        txt_shortbio.setText(dataSnapshot.child("shortbio").getValue().toString());
                    if(!dataSnapshot.child("gender").getValue().toString().equals(null))
                        txt_gender.setText(dataSnapshot.child("gender").getValue().toString());
                    if(!dataSnapshot.child("city").getValue().toString().equals(null))
                        txt_city.setText(dataSnapshot.child("city").getValue().toString());
                    if(!dataSnapshot.child("interests").getValue().toString().equals(null))
                        txt_interests.setText(dataSnapshot.child("interests").getValue().toString());
                    if(!dataSnapshot.child("userpic").getValue().toString().equals(null))
                        Glide.with(getContext()).load(dataSnapshot.child("userpic").getValue().toString()).into(profpic);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;

    }
    private String getExtension(Uri uri)
    {
        ContentResolver cr = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));

    }
    public void startIntent(int requestcode){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,requestcode);

    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && data !=null && data.getData()!=null){

            imgprofuri = data.getData();
            profpic.setImageURI(imgprofuri);
        }

    }
    private void imageUploader(Uri uri){
        final StorageReference Ref = storageReference.child(System.currentTimeMillis() + "." + getExtension(uri));
        UploadTask uploadTask = Ref.putFile(uri);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return Ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String imagename =downloadUri.toString();
                    users.setUserpic(imagename);
                    if(requestcode==1){
                       databaseReferenceUsers.setValue(users);
                    }
                } else {
                    // Toast.makeText(getActivity(), "An error has occuredd", Toast.LENGTH_SHORT).show();
                }
            }
        });

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                int currentprogress = (int) progress;
                progressbar.setProgress(currentprogress);
            }
        });


    }
}
