package fhku.mytrips;

/**
 * Created by Jojo on 09.01.2018.
 */

public class Country {
    private int id;
    private double latitude;
    private double longitude;
    private String countryName;

    public Country(int id, double longitude, double latitude, String countryName) {
        this.id = id;
        this.latitude=latitude;
        this.longitude=longitude;
        this.setCountryName(countryName);

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

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}

