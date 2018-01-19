package fhku.mytrips;

/**
 * Created by Jojo on 09.01.2018.
 */

public class Country {
    private int id;
    private double latitude;
    private double longitude;

    public Country(int id, double longitude, double latitude) {
        this.id = id;
        this.latitude=latitude;
        this.longitude=longitude;

    }

    public Country(double longitude, double latitude) {
        this.latitude=latitude;
        this.longitude=longitude;

    }

    public Country() {
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}

