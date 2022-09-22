package com.mustafaunlu.foursquareclone.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.mustafaunlu.foursquareclone.R;
import com.mustafaunlu.foursquareclone.databinding.ActivityMapsBinding;
import com.mustafaunlu.foursquareclone.model.Place;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    //LocationManager & LocationListener
    private LocationManager locationManager;
    private LocationListener locationListener;

    //Launcher
    private ActivityResultLauncher<String> permissionLauncher;

    //Selected Latitude & Longitude
    private String selectedLatitudeString;
    private String selectedLongitudeString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        registerLauncher();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

        //mMap=googleMap;
        //mMap.setOnMapLongClickListener(this);



        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                Snackbar.make(binding.getRoot(),"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                    }
                }).show();
            }else{
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);

            }
        }else{
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);

        }



    }
    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        selectedLatitudeString=String.valueOf(latLng.latitude);
        selectedLongitudeString=String.valueOf(latLng.longitude);

        mMap.addMarker(new MarkerOptions().position(latLng).title("New Place"));
        Toast.makeText(this, "Click on save", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.save){
            // to MainActivity
            upload();
        }
        return super.onOptionsItemSelected(item);
    }

    private void upload() {

        Place place=Place.getInstance();

        String placeName=place.getName();
        String placeType=place.getType();
        String placeAtmosphere=place.getAtmosphere();
        /*
        Bitmap placeImage=place.getImage();

        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        placeImage.compress(Bitmap.CompressFormat.PNG,50,byteArrayOutputStream);
        byte[] bytes=byteArrayOutputStream.toByteArray();

        ParseFile parseFile=new ParseFile("image.pnp",bytes);
 */
        ParseObject parseObject=new ParseObject("Places");
        //parseObject.put("image",parseFile);
        parseObject.put("name",placeName);
        System.out.println("name: "+placeName);
        parseObject.put("type",placeType);
        System.out.println("type: "+placeType);

        parseObject.put("atmosphere",placeAtmosphere);
        parseObject.put("latitude",selectedLatitudeString);
        parseObject.put("longitude",selectedLongitudeString);


        parseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if( e != null){
                    Toast.makeText(MapsActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }else{
                    Intent intent=new Intent(MapsActivity.this,MainActivity.class);
                    startActivity(intent);
                }
            }
        });


    }

    private void registerLauncher() {

        permissionLauncher=this.registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    //permission granted!!


                    locationManager = (LocationManager) MapsActivity.this.getSystemService(Context.LOCATION_SERVICE);
                    locationListener=new LocationListener() {
                        @Override
                        public void onLocationChanged(@NonNull Location location) {
                            SharedPreferences sharedPreferences=MapsActivity.this.getSharedPreferences("com.mustafaunlu.foursquareclone",MODE_PRIVATE);
                            boolean firstTry=sharedPreferences.getBoolean("firstTime",true);

                            if(firstTry){
                                LatLng userLocation=new LatLng(location.getLatitude(),location.getLongitude());
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15));
                                sharedPreferences.edit().putBoolean("firstTime",false).apply();
                            }
                        }
                    };

                    if(ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION) ==PackageManager.PERMISSION_GRANTED){

                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                        mMap.clear();

                        Location lastKnownLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if(lastKnownLocation != null){
                            LatLng lastUserLocation=new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15));
                        }
                    }


                }else{
                    //permission denied
                    Toast.makeText(MapsActivity.this, "Permission needed!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


}