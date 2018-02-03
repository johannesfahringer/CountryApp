package fhku.mytrips;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Jojo on 09.01.2018.
 */

public class Database extends SQLiteOpenHelper{

    // Database Version
    public static final int DATABASE_VERSION = 1;
    // Database Name
    public static final String DATABASE_NAME = "Tracking.db";
    // Contacts table name
    public static final String TABLE_COUNTRY = "country";
    // Shops Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_COUNTRYNAME = "countryname";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }




    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_COUNTRY + "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                KEY_LATITUDE + " REAL, " +
                KEY_LONGITUDE + " REAL, " +
                KEY_COUNTRYNAME + " TEXT )"
        );

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COUNTRY);
        onCreate(db);
    }

    public boolean insertCountry(double latitude, double longitude, String countryName) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_LATITUDE, latitude);
        contentValues.put(KEY_LONGITUDE, longitude);
        contentValues.put(KEY_COUNTRYNAME, countryName);
        db.insert(TABLE_COUNTRY, null, contentValues);
        return true;
    }

    public boolean updatecountry(Integer id, double latitude, double longitude, String countryName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_LATITUDE, latitude);
        contentValues.put(KEY_LONGITUDE, longitude);
        contentValues.put(KEY_COUNTRYNAME, countryName);
        db.update(TABLE_COUNTRY, contentValues, KEY_ID + " = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Cursor getCountry(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String s= "\"=?\"";
        Cursor c = db.rawQuery( "SELECT * FROM " + TABLE_COUNTRY + " WHERE " +
                KEY_ID + s, new String[] { Integer.toString(id) } );
        return c;
    }



    public Cursor getAllCountries() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "SELECT * FROM " + TABLE_COUNTRY, null );
        return res;
    }





}

