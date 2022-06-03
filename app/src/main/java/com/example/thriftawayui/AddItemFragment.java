package com.example.thriftawayui;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import static android.app.Activity.RESULT_OK;

public class AddItemFragment extends Fragment {
    @Nullable

    EditText txt_itemname, txt_description, txt_tags;
    Button btn_save, btn_cancel;
    DatabaseReference databaseReferenceImages;
    DatabaseReference databaseReferenceItems;
    StorageReference storageReference;
    ImageView image1, image2, image3;
    Items item;
    Images image;
    long maxid=0;
    public Uri imguri1, imguri2, imguri3;
    public int requestcode;
    ProgressBar progressbar;
    String imagename1, imagename2, imagename3;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_additem, container, false );
        txt_itemname = view.findViewById(R.id.txtitem);
        txt_description = view.findViewById(R.id.txtdescription);
        btn_save = view.findViewById(R.id.btnsaveitem);
        btn_cancel = view.findViewById(R.id.btncancelitem);

        image1 = view.findViewById(R.id.img1);
        image2 = view.findViewById(R.id.img2);
        image3 = view.findViewById(R.id.img3);

        progressbar = view.findViewById(R.id.progressadditem);

        storageReference = FirebaseStorage.getInstance().getReference("Images");
        databaseReferenceItems= FirebaseDatabase.getInstance().getReference().child("Items");
        item = new Items();
        image = new Images();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userid = user.getUid();

        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestcode = 1;
                startIntent(requestcode);

            }
        });
        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestcode =2;
               startIntent(requestcode);
            }
        });
        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestcode =3;
                startIntent(requestcode);
            }
        });

        databaseReferenceItems.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    maxid = (dataSnapshot.getChildrenCount());

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String itemname = txt_itemname.getText().toString();
                String itemdesc = txt_description.getText().toString();


                item.setItemname(itemname);
                item.setItemdesc(itemdesc);
                item.setUploadedby(userid);


                if(requestcode == 1){
                    imageUploader1(imguri1);

                }if(requestcode ==2){

                    imageUploader1(imguri1);
                    imageUploader2(imguri2);

                }if(requestcode==3){

                    imageUploader1(imguri1);
                    imageUploader2(imguri2);
                    imageUploader3(imguri3);
                }


            }
        });


        return view;
    }
    public void startIntent(int requestcode){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,requestcode);

    }
    private String getExtension(Uri uri)
    {
        ContentResolver cr = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));

    }

    /*    private void imageUploader(Uri uri) {

        StorageReference Ref = storageReference.child(System.currentTimeMillis() + "." + getExtension(uri));
        Ref.putFile(uri)

                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //progressDialog.dismiss();

                            imagename1 = taskSnapshot.getStorage().getDownloadUrl().toString();
                            item.setImage1(imagename1);
                            if(requestcode==1){

                                databaseReferenceItems.child(String.valueOf(maxid+1)).setValue(item);
                            }

                         }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        //progressDialog.dismiss();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        int currentprogress = (int) progress;
                        progressbar.setProgress(currentprogress);
                    }
                });
    }*/
    private void imageUploader1(Uri uri){
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
                    imagename1 =downloadUri.toString();
                    image.setImage1(imagename1);
                    if(requestcode==1){
                        String id= databaseReferenceItems.push().getKey();
                        item.setItemid(id);
                        databaseReferenceItems.child(id).setValue(item);
                        databaseReferenceImages= FirebaseDatabase.getInstance().getReference("Items").child(id);
                        databaseReferenceImages.child("images").setValue(image);
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
    private void imageUploader2(Uri uri2) {

        final StorageReference Ref2 = storageReference.child(System.currentTimeMillis() + "." + getExtension(uri2));
        UploadTask uploadTask = Ref2.putFile(uri2);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return Ref2.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    imagename2 =downloadUri.toString();
                    image.setImage2(imagename2);
                    if(requestcode==2){
                        String id= databaseReferenceItems.push().getKey();
                        item.setItemid(id);
                        databaseReferenceImages= FirebaseDatabase.getInstance().getReference("Items").child(id);
                        databaseReferenceItems.child(id).setValue(item);
                        databaseReferenceImages.child("images").setValue(image);
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
    private void imageUploader3(Uri uri3) {

        final StorageReference Ref3 = storageReference.child(System.currentTimeMillis() + "." + getExtension(uri3));
        UploadTask uploadTask = Ref3.putFile(uri3);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return Ref3.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    imagename3 =downloadUri.toString();
                    image.setImage3(imagename3);
                    if(requestcode==3){
                        String id= databaseReferenceItems.push().getKey();
                        item.setItemid(id);
                        databaseReferenceImages= FirebaseDatabase.getInstance().getReference("Items").child(id);
                        databaseReferenceItems.child(id).setValue(item);
                        databaseReferenceImages.child("images").setValue(image);

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


    private void removeStack(){
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data !=null && data.getData()!=null)
        {
            imguri1 = data.getData();
            image1.setImageURI(imguri1);
        }
        if(requestCode==2 && resultCode==RESULT_OK && data !=null && data.getData()!=null)
        {
            imguri2 = data.getData();
            image2.setImageURI(imguri2);
        }
        if(requestCode==3 && resultCode==RESULT_OK && data !=null && data.getData()!=null)
        {
            imguri3 = data.getData();
            image3.setImageURI(imguri3);
        }
    }
}
