package fhku.mytrips;

import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.provider.Settings;
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
import android.widget.Toast;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback {

    LocationManager lm;
    GoogleMap map;
    Database myDb;
    Double[] latitudeArray;
    Double[] longitudeArray;
    String[] countryNameArray;
    int[] parsedIdArray;
    Cursor res;
    int i;
    Double latitude1;
    Double longitude1;
    String countryName;
    boolean checkLocationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDb = new Database(this);
        checkLocationListener = true;
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        addLocationListener();

        Button gps = (Button) findViewById(R.id.gps);
        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
    }

    public void startGPS() {
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        addLocationListener();
    }

    public void endGPS() {
        lm.removeUpdates(this);
        lm = null;
    }

    public void addLocationListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.i("LOCATION", "permission granted");
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 20000, this);
            } else {
                requestPermissions(new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, 7);

            }
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 7 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            addLocationListener();
        }
    }


    public void showLocations() {
        //Der Cursor holt sich alle Länder die in der Datenbank gespeichert sind.
        res = myDb.getAllCountries();

        //Wenn die Anzahl der Ländereinträge in der Datenbank kleiner als 1 sind wird ein Log gemacht.
        if (res.getCount() < 1) {
            Log.i("sLocation kein Eintrag", "Länderanzahl in der Datenbank : " + String.valueOf(res.getCount()));
        }

        //Wenn die Anzahl der Länder in der Datenbank größer 1 ist wird der folgende Part ausgeführt.
        if (res.getCount() > 0) {

            // int countrySize, ist die Gesamtanzahl der Länder in der Tabelle Country, Stringbuffer wird erstellt.
            int countrySize = res.getCount();
            StringBuffer buffer = new StringBuffer();

            //Index für Array
            i = 0;

            //Erstellen von Arrays für Latitude, Longitude, Id und Countryname mit der größe Der gesamte Länder in der Datenbank.
            latitudeArray = new Double[countrySize];
            longitudeArray = new Double[countrySize];
            parsedIdArray = new int[countrySize];
            countryNameArray = new String[countrySize];

            //Springt immer zur nächsten Zeile in der Tabelle Country, solange es keine Einträge mehr gibt.
            while (res.moveToNext()) {

                //Stringbuffer für die ANzeige im Log ob Länder in der Datenbank vorhanden sind.
                buffer.append("ID: " + res.getString(0) + "\n");
                buffer.append("Latitude :" + res.getString(1) + "\n");
                buffer.append("Longitude :" + res.getString(2) + "\n");
                buffer.append("Countryname :" + res.getString(3) + "\n");

                //Im nächsten Schritt werden jeweils Einträge einer Spalte in die jeweilige Variable gespeichert
                int id = Integer.parseInt(res.getString(0));
                latitude1 = Double.parseDouble(res.getString(1));
                longitude1 = Double.parseDouble(res.getString(2));
                countryName = res.getString(3);

                //Im nächsten Schritt werden an der Stelle i, beginnend bei 0 die Einträge der Variablen bevor in das jeweilige Array gespeichert
                latitudeArray[i] = latitude1;
                longitudeArray[i] = longitude1;
                parsedIdArray[i] = id;
                countryNameArray[i] = countryName;

                //Für jeden geladene Zeile der Tabelle wird ein Marker gesetzt mit dem Titel Ländername und an der Stelle Longitude, Latitude
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.title(countryNameArray[i]);
                markerOptions.position(new LatLng(latitudeArray[i], longitudeArray[i]));
                map.addMarker(markerOptions);

                //Index zählt um ein hoch
                i++;
            }
            //Ausgabe der Einträge und der Gesamtzahl der Einträge
            Log.i("DATABASE", buffer.toString());
            Log.i("EINTRÄGE", String.valueOf(res.getCount()));
        }
    }


    public void onLocationChanged(Location location) {

        if (map != null) {

   /*Dieser String soll an die Methode "getCountryNames" die aktuellen Längen/Breitengrade bei Lokationsänderung übergeben
   und erhält durch den Geocoder den Ländernamen der übergebenen Koordinaten.*/
            String currentCountryName = getCountryNames(location.getLongitude(), location.getLatitude());

            //Prüft ob ein Land bereits exestiert oder nicht.
            boolean doesExists = false;


   /*Wenn bei Änderung der Lokation noch kein Eintrag in der Datenbank vorhanden ist,
    * wird ein Insert-Statement gemacht. Dabei wird die Methode insertCountry von der
    * Klasse Database aufgerufen und folgende Parameter übergeben
    * (aktuelle Breitengrade der Position,aktuelle Längengrade der Position und der
    * aktuelle Ländername der im String currentCountryName gespeichert wurde.
    * Danach wird der Marker an der aktuellen Position mit dem Titel des aktuellen Ländernamens.
    * Und ein Log wird ausgegeben mit Längen und Breitengrade.*/
            if (res.getCount() == 0) {
                myDb.insertCountry(location.getLatitude(), location.getLongitude(), currentCountryName);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(location.getLatitude(), location.getLongitude()));
                markerOptions.title(currentCountryName);
                Log.i("LOCATION", location.getLatitude() + " : " + location.getLongitude());

            }

   /*Solange er Einträge findet geht der der Cursor durch jede Zeile und überprüft ob der Name
   des Landes der aktuellen Position bereits in der Datenbank vorhanden ist. Wenn dies der
   Fall ist setzt er "doesExists" auf "true" geht mit dem Curor wieder zur ersten Zeile und
   bricht die while Schleife ab.*/
            while (res.moveToNext()) {
                Log.i("Current country", String.valueOf(currentCountryName));
                Log.i("Cursor String", String.valueOf(res.getString(3)));
                if (currentCountryName.equals(res.getString(3))) {
                    // boolean isInserted = myDb.insertCountry(location.getLatitude(), location.getLongitude(), currentCountryName);
                    doesExists = true;
                    res.moveToFirst();
                    break;
                }
            }

   /* Wenn der aktuelle Ländername der aktuellen Position noch nicht vorhanden ist, dann wird der Name inklusive
    Längen- und Breitengrade in die Datenbank gespeichert, ein Marker gesetzt und die Activity wird neu geladen.*/
            if (doesExists == false) {
                boolean isInserted = myDb.insertCountry(location.getLatitude(), location.getLongitude(), currentCountryName);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(location.getLatitude(), location.getLongitude()));
                markerOptions.title(currentCountryName);
                //Log.i("COUNTRYNAME", currentCountryName);
                startActivity(new Intent(this, MainActivity.class));

                if (isInserted == true) {
                    Toast.makeText(MainActivity.this, "You have not been in this country", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "DATA NOT INSERTED", Toast.LENGTH_LONG).show();
                }
                res.moveToFirst();
            }
        }
    }


 /*Countryname wird durch den Geocoder geholt dafür muss man, Longitude und Latitude übergeben*/
    public String getCountryNames(double longitude, double latitude) {
        {
            Geocoder geocoder = new Geocoder(this);
            List < Address > addresses;

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                countryName = addresses.get(0).getCountryName();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.i("KKKKKK", String.valueOf(countryName));
        return countryName;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}

    @Override
    public void onProviderEnabled(String s) {}

    @Override
    public void onProviderDisabled(String s) {}

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        showLocations();
    }
}