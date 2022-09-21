package com.mustafaunlu.foursquareclone.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.mustafaunlu.foursquareclone.databinding.ActivityPlaceBinding;
import com.mustafaunlu.foursquareclone.model.Place;

import java.io.IOException;

public class PlaceActivity extends AppCompatActivity {

    private ActivityPlaceBinding binding;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private ActivityResultLauncher<String> permissionLauncher;
    Bitmap selectedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPlaceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        registerLauncher();
    }



    public void next(View view){
        //Save data
        Place place=new Place();
        place.setName(binding.nameEditText.getText().toString());
        place.setType(binding.typeEditText.getText().toString());
        place.setAtmosphere(binding.atmosphereEditText.getText().toString());
        place.setImage(selectedImage);

        //Intent
        Intent intent=new Intent(PlaceActivity.this,MapsActivity.class);
        startActivity(intent);
    }

    public void select(View view){

        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            //request permission
            if(ActivityCompat.shouldShowRequestPermissionRationale(PlaceActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //request permission -> launcher
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

                    }
                }).show();
            }else{
                System.out.println("activity compat else");
                //request permission -> launcher
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }else{
            //to gallery -> launcher yazılacak
            Intent intentToGallery=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);
        }
    }


    private void registerLauncher(){
        System.out.println("register");
        activityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                if(result.getResultCode()==RESULT_OK){
                    Intent intentFromResult=result.getData();
                    if(intentFromResult != null){
                        Uri imageData=intentFromResult.getData();
                        try{
                            if(Build.VERSION.SDK_INT > 28){
                                ImageDecoder.Source source=ImageDecoder.createSource(getContentResolver(),imageData);
                                selectedImage= ImageDecoder.decodeBitmap(source);
                                binding.imageView.setImageBitmap(selectedImage);
                            }else{
                                selectedImage= MediaStore.Images.Media.getBitmap(getContentResolver(),imageData);
                                binding.imageView.setImageBitmap(selectedImage);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }
        });

        permissionLauncher=registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    //permission granted -> activityResultLauncher
                    System.out.println("accepted");
                    Intent intentToGallery=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);


                }else{
                    //permission denied
                    System.out.println("denied");
                    Toast.makeText(PlaceActivity.this, "Permission needed", Toast.LENGTH_LONG).show();

                }
            }
        });



    }
}