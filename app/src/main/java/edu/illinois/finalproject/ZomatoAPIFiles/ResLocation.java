package edu.illinois.finalproject.ZomatoAPIFiles;

import android.os.Parcel;
import android.os.Parcelable;

public class ResLocation implements Parcelable {
    private String address;
    private String locality;
    private String city;
    private float latitude;
    private float longitude;
    private String zipcode;
    private int country_id;

    public ResLocation() {

    }

    public String getAddress() {
        return address;
    }

    public String getLocality() {
        return locality;
    }

    public String getCity() {
        return city;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public String getZipcode() {
        return zipcode;
    }

    public int getCountry_id() {
        return country_id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.address);
        dest.writeString(this.locality);
        dest.writeString(this.city);
        dest.writeFloat(this.latitude);
        dest.writeFloat(this.longitude);
        dest.writeString(this.zipcode);
        dest.writeInt(this.country_id);
    }

    protected ResLocation(Parcel in) {
        this.address = in.readString();
        this.locality = in.readString();
        this.city = in.readString();
        this.latitude = in.readFloat();
        this.longitude = in.readFloat();
        this.zipcode = in.readString();
        this.country_id = in.readInt();
    }

    public static final Creator<ResLocation> CREATOR = new Creator<ResLocation>() {
        @Override
        public ResLocation createFromParcel(Parcel source) {
            return new ResLocation(source);
        }

        @Override
        public ResLocation[] newArray(int size) {
            return new ResLocation[size];
        }
    };
}
