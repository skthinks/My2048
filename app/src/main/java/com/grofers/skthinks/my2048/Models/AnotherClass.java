package com.grofers.skthinks.my2048.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.AutoParcel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Skthinks on 28/06/17.
 */
@AutoParcel(isParcel = false)
@Setter
@Getter
@NoArgsConstructor
public class AnotherClass implements Parcelable {

    int arsenal;

    protected AnotherClass(Parcel in) {
        arsenal = in.readInt();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(arsenal);
    }

    public static final Creator<AnotherClass> CREATOR = new Creator<AnotherClass>() {
        @Override
        public AnotherClass createFromParcel(Parcel in) {
            return new AnotherClass(in);
        }

        @Override
        public AnotherClass[] newArray(int size) {
            return new AnotherClass[size];
        }
    };
}
