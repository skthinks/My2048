package com.grofers.skthinks.my2048.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.AutoParcel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Skthinks on 18/11/16.
 */
@AutoParcel
@Setter
@Getter
@NoArgsConstructor
public class Something implements Parcelable {

    int count;

    ObjFields objFields;

    protected Something(Parcel in) {
        this.count = in.readInt();
        this.objFields = in.readParcelable(ObjFields.CREATOR.getClass().getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(count);
        dest.writeParcelable(objFields, 0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Something> CREATOR = new Parcelable.Creator<Something>() {
        @Override
        public Something createFromParcel(Parcel in) {
            return new Something(in);
        }

        @Override
        public Something[] newArray(int size) {
            return new Something[size];
        }
    };
}
