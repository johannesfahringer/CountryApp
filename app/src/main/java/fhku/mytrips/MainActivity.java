package fhku.mytrips;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static android.net.wifi.WifiConfiguration.Status.strings;

public class MainActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback {

    LocationManager lm;
    GoogleMap map;
    Database myDb;
    Double [] latitudeArray;
    Double [] longitudeArray;
    ArrayList <Double> latitudeList = new ArrayList<>();
    ArrayList <Double> longitudelist = new ArrayList<>();
    int [] parsedIdArray;
    Marker[] markedCountries;
    Cursor res;
    int i;
    Double latitude1;
    Double longitude1;
    TextView longitude, latitude, id;
    ArrayList<Marker> countryList = new ArrayList<>();
    public final static String KEY_EXTRA_COUNTRY_ID = "KEY_ID";


    //GEOJSON
    //http://abhiandroid.com/programming/json
    //https://github.com/datasets/geo-countries/tree/master/data
    //http://thematicmapping.org/downloads/world_borders.php
    //https://github.com/google/gson


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDb = new Database(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        lm = (LocationManager)  getSystemService(LOCATION_SERVICE);
        addLocationListener();
        //showLocations();






    }

    public void addLocationListener () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.i("LOCATION", "permission granted");
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 20000, this );
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 7);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 7 && grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            addLocationListener();
        }
    }

    public void showLocations(){
        res = myDb.getAllCountries();
        int countrySize = res.getCount();
        if (res.getCount() == 0){
            Log.i("ERROR", "Nothing found");
            return;
        }
        if (res.getCount()>0){
        StringBuffer buffer = new StringBuffer();
        i=0;
        latitudeArray= new Double[countrySize];
        longitudeArray = new Double[countrySize];
        parsedIdArray = new int[countrySize];
        markedCountries = new Marker[countrySize];

        while (res.moveToNext()){

            buffer.append("ID: " +res.getString(0)+"\n");
            buffer.append("Latitude :"+ res.getString(1)+"\n");
            buffer.append("Longitude :"+ res.getString(2)+"\n");

            int id = Integer.parseInt(res.getString(0));
            latitude1 = Double.parseDouble(res.getString(1));
            longitude1 = Double.parseDouble(res.getString(2));






            latitudeArray[i]=latitude1;
            longitudeArray[i]=longitude1;
            parsedIdArray[i]=id;

            map.addMarker(new MarkerOptions()
                    .position(new LatLng(latitudeArray[i], longitudeArray[i]))
                    .title(String.valueOf(latitudeArray[i] + ": " + longitudeArray[i]))
            );







            Log.i("IDasdf", String.valueOf(parsedIdArray[1]));

            Intent values = new Intent(this, ListActivity.class);

            Log.i("Position", String.valueOf(latitudeArray[i]));

            i++;

        }


        Log.i("DATABASE", buffer.toString());
        Log.i("EINTRÄGE", String.valueOf(res.getCount()));
        /*latitudeList.add(Double.parseDouble(res.getString(1)));
            for (Double list:latitudeList){
                Log.i("Liste", list.toString());
            }
        longitudelist.add(i, longitude1);*/
    }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("LOCATION", location.getLatitude() + " : "+ location.getLongitude());

        if (map != null){
            MarkerOptions mo = new MarkerOptions();
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            mo.title("My Location");
            mo.position(new LatLng(location.getLatitude(), location.getLongitude()));

            map.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude())));






            boolean isInserted = myDb.insertCountry(location.getLatitude(), location.getLongitude());
            if (isInserted == true){
                Toast.makeText(MainActivity.this, "DATA INSERTED", Toast.LENGTH_SHORT).show();
                map.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude())));
            }else {
                Toast.makeText(this, "DATA NOT INSERTED", Toast.LENGTH_LONG).show();
            }



            //map.moveCamera(CameraUpdateFactory.newLatLng(ll));

        }


    }

    public void loadLocations(){

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        showLocations();









        // Add a marker to the map at the ‘Sydney’ coordinates. Unless you specify otherwise,
        // Android uses Google Maps’ standard marker icon, but you can customize this icon by
        // changing its colour, image or anchor point, if required.


        // Use CameraUpdate to move the map’s ‘camera’ to the user's current location - in this
        // example,that’s the hard-coded Sydney coordinates. When you’re creating your own apps,
        // you may want to tweak this line to animate the camera’s movements, which typically
        // provides a better user experience. To animate the camera, replace GoogleMap.moveCamera
        // with GoogleMap.animateCamera//

    }

}




