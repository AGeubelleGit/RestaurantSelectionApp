package edu.illinois.finalproject.ZomatoAPIFiles;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by alexandregeubelle on 10/30/17.
 */

public class UserRating implements Parcelable {
    private float aggregate_rating;
    private String rating_text;
    private String rating_color;
    private int votes;

    public UserRating() {

    }

    public float getAggregate_rating() {
        return aggregate_rating;
    }

    public String getRating_text() {
        return rating_text;
    }

    public String getRating_color() {
        return rating_color;
    }

    public int getVotes() {
        return votes;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.aggregate_rating);
        dest.writeString(this.rating_text);
        dest.writeString(this.rating_color);
        dest.writeInt(this.votes);
    }

    protected UserRating(Parcel in) {
        this.aggregate_rating = in.readFloat();
        this.rating_text = in.readString();
        this.rating_color = in.readString();
        this.votes = in.readInt();
    }

    public static final Creator<UserRating> CREATOR = new Creator<UserRating>() {
        @Override
        public UserRating createFromParcel(Parcel source) {
            return new UserRating(source);
        }

        @Override
        public UserRating[] newArray(int size) {
            return new UserRating[size];
        }
    };
}

