package com.grofers.skthinks.my2048.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.GenerateTest;

import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Skthinks on 18/11/16.
 */
@GenerateTest(usesParcellerAnnotation = false)
@Setter
@Getter
@NoArgsConstructor
public class Something implements Parcelable {

    ObjFields objFields;

    Set<Integer> integerSet;

    int count;

    String abc;

    List<ObjFields> objFieldsList;

    List<Integer> stuff;

    List<String> strings;

    Map<String, Integer> things;

    Map<Integer, ObjFields> mapping;

    protected Something(Parcel in) {
        this.count = in.readInt();
        this.objFields = in.readParcelable(ObjFields.CREATOR.getClass().getClassLoader());
        in.readTypedList(this.objFieldsList, ObjFields.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(count);
        dest.writeParcelable(objFields, 0);
        dest.writeTypedList(objFieldsList);
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
