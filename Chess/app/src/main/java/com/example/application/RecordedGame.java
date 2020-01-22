package com.example.application;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RecordedGame implements Parcelable {
    protected ArrayList<Integer> moves;
    protected String title;
    protected Date date;

    public RecordedGame(ArrayList<Integer> moves, String title, Date date){
        this.moves = moves;
        this.title = title;
        this.date = date;
    }

    public ArrayList<Integer> getMoves(){
        return moves;
    }

    public String getTitle(){
        return title;
    }

    public Date getDate(){
        return date;
    }

    public String toString() {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String convertDate = dateFormat.format(this.date);
            String titleDate = this.title + " - " + convertDate;
            return titleDate;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.moves);
        dest.writeString(this.title);
        dest.writeLong(this.date != null ? this.date.getTime() : -1);
    }

    protected RecordedGame(Parcel in) {
        this.moves = new ArrayList<Integer>();
        in.readList(this.moves, Integer.class.getClassLoader());
        this.title = in.readString();
        long tmpDate = in.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
    }

    public static final Parcelable.Creator<RecordedGame> CREATOR = new Parcelable.Creator<RecordedGame>() {
        @Override
        public RecordedGame createFromParcel(Parcel source) {
            return new RecordedGame(source);
        }

        @Override
        public RecordedGame[] newArray(int size) {
            return new RecordedGame[size];
        }
    };
}
