package fhku.mytrips;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class ListActivity extends AppCompatActivity {

    Database myDb;
    TextView longitude, latitude, id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        id=findViewById(R.id.id);
        latitude=findViewById(R.id.latitude);
        longitude=findViewById(R.id.longitude);

        String id1 = (String) getIntent().getExtras().get("idArray");
        Log.i("TEXT", id1);
        String latitude1 = (String) getIntent().getExtras().get("latitudeArray");
        String longitude1 = (String) getIntent().getExtras().get("longitudeArray");

        id.setText("ID: " + id1);
        latitude.setText("LATITUDE: " + latitude1);
        longitude.setText("LONGITUDE: " + longitude1);








    }






}
