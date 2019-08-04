package com.utkarsh.hikerswatch;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    LocationManager manager;
    LocationListener listener;
    TextView latTextView;
    TextView longTextView;
    TextView accTextView;
    TextView altTextView;
    TextView addTextView;

    public void updateLocationInfo(Location location){

        latTextView.setText("Latitude: "+location.getLatitude());
        longTextView.setText("Longitude: "+location.getLongitude());
        accTextView.setText("Accuracy: "+location.getAccuracy());
        altTextView.setText("Altitude: "+location.getAltitude());


        Geocoder coder=new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            String address="Could not find address";
            List<Address> listAddress = coder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if(address!=null && listAddress.size()>0){
                address="Address: \n";

                if(listAddress.get(0).getSubThoroughfare()!=null){
                    address=address+listAddress.get(0).getSubThoroughfare()+" ";
                }
                if(listAddress.get(0).getThoroughfare()!=null){
                    address=address+listAddress.get(0).getThoroughfare()+"\n ";
                }
                if(listAddress.get(0).getLocality()!=null){
                    address=address+listAddress.get(0).getLocality()+"\n ";
                }
                if(listAddress.get(0).getPostalCode()!=null){
                    address=address+listAddress.get(0).getPostalCode()+"\n ";
                }
                if(listAddress.get(0).getCountryName()!=null){
                    address=address+listAddress.get(0).getCountryName();
                }
            }
            addTextView.setText(address);

        }
        catch (IOException e){
            e.printStackTrace();
        }



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 5, listener);

                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latTextView=(TextView) findViewById(R.id.latTextView);
        longTextView=(TextView) findViewById(R.id.longTextView);
        accTextView=(TextView) findViewById(R.id.accTextView);
        altTextView=(TextView) findViewById(R.id.altTextView);
        addTextView=(TextView) findViewById(R.id.addTextView);

        manager=(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        listener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateLocationInfo(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (Build.VERSION.SDK_INT<23){
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 5, listener);
        }
        else {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }else {
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 5, listener);

                Location lastLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (lastLocation != null) {
                    updateLocationInfo(lastLocation);
                }
            }
        }
    }
}
