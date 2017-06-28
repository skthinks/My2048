package com.grofers.skthinks.my2048;

import android.os.Parcel;

import com.grofers.skthinks.my2048.Models.ObjFields;

import junit.framework.Assert;

import org.junit.Test;


public class ObjFieldsTest {

    @Test
    public void testObjFields() {
        ObjFields testObjFields = new ObjFields();

        // Obtain a Parcel object and write the parcelable object to it:
        Parcel parcel = Parcel.obtain();
        testObjFields.writeToParcel(parcel,0);

        // After you're done with writing, you need to reset the parcel for reading:
        parcel.setDataPosition(0);

        // Reconstruct object from parcel and asserts:
        ObjFields createdFromParcel = ObjFields.CREATOR.createFromParcel(parcel);

        Assert.assertEquals(testObjFields, createdFromParcel);
    }

    @Test
    public void testObjFieldsParams() {
        ObjFields testObjFields = new ObjFields();
		testObjFields.setTotal(1);
		testObjFields.setName("String");
		testObjFields.setDucks(1);


        // Obtain a Parcel object and write the parcelable object to it:
        Parcel parcel = Parcel.obtain();
        testObjFields.writeToParcel(parcel,0);

        // After you're done with writing, you need to reset the parcel for reading:
        parcel.setDataPosition(0);

        // Reconstruct object from parcel and asserts:
        ObjFields createdFromParcel = ObjFields.CREATOR.createFromParcel(parcel);

        Assert.assertEquals(testObjFields, createdFromParcel);
    }
}
