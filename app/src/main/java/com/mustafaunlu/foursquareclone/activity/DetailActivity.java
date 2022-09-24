package com.mustafaunlu.foursquareclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mustafaunlu.foursquareclone.R;
import com.mustafaunlu.foursquareclone.databinding.ActivityDetailBinding;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ActivityDetailBinding binding;
    private GoogleMap mMap;

    private String placeName;
    private String latitudeString;
    private String longitudeString;
    private Double latitude;
    private Double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent=getIntent();
        placeName=intent.getStringExtra("name");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.detailMap);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        
        mMap=googleMap;
        getData();

    }

    private void getData() {

        ParseQuery<ParseObject> query=ParseQuery.getQuery("Places");
        query.whereEqualTo("name",placeName);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e != null){
                    Toast.makeText(DetailActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }else{

                    if(objects.size() > 0){
                        for(ParseObject object : objects){

                            ParseFile parseFile=(ParseFile) object.get("image");
                            parseFile.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if(e == null && data!= null){
                                        Bitmap bitmap= BitmapFactory.decodeByteArray(data,0, data.length);
                                        binding.detailImageView.setImageBitmap(bitmap);

                                        binding.detailNameTextView.setText(placeName);
                                        binding.detailTypeTextView.setText(object.getString("type"));
                                        binding.detailAtmosphereTextView.setText(object.getString("atmosphere"));

                                        latitudeString=object.getString("latitude");
                                        longitudeString=object.getString("longitude");

                                        latitude=Double.parseDouble(latitudeString);
                                        longitude=Double.parseDouble(longitudeString);

                                        mMap.clear();;

                                        LatLng placeLocation=new LatLng(latitude,longitude);
                                        mMap.addMarker(new MarkerOptions().title(placeName).position(placeLocation));
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLocation,15));


                                    }
                                }
                            });
                        }
                    }
                }
            }
        });


    }
}