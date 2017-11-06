package com.grofers.skthinks.my2048.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.GenerateTest;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Skthinks on 12/05/17.
 */
@GenerateTest(usesParcellerAnnotation = false)
@Setter
@Getter
@NoArgsConstructor
public class ObjFields implements Parcelable {

    AnotherClass somethingElse;

    int total;

    String name;

    int ducks;

    protected ObjFields(Parcel in) {
        this.total = in.readInt();
        this.name = in.readString();
        this.ducks = in.readInt();
        this.somethingElse = in.readParcelable(AnotherClass.CREATOR.getClass().getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(total);
        dest.writeString(name);
        dest.writeInt(ducks);
        dest.writeParcelable(somethingElse, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ObjFields> CREATOR = new Creator<ObjFields>() {
        @Override
        public ObjFields createFromParcel(Parcel in) {
            return new ObjFields(in);
        }

        @Override
        public ObjFields[] newArray(int size) {
            return new ObjFields[size];
        }
    };
}
