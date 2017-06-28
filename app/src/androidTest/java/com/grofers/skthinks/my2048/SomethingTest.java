package com.grofers.skthinks.my2048;

import android.os.Parcel;

import com.grofers.skthinks.my2048.Models.ObjFields;
import com.grofers.skthinks.my2048.Models.Something;

import junit.framework.Assert;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class SomethingTest {

    @Test
    public void testSomething() {
        Something testSomething = new Something();

        // Obtain a Parcel object and write the parcelable object to it:
        Parcel parcel = Parcel.obtain();
        testSomething.writeToParcel(parcel,0);

        // After you're done with writing, you need to reset the parcel for reading:
        parcel.setDataPosition(0);

        // Reconstruct object from parcel and asserts:
        Something createdFromParcel = Something.CREATOR.createFromParcel(parcel);

        Assert.assertEquals(testSomething, createdFromParcel);
    }

    @Test
    public void testSomethingParams() {
        Something testSomething = new Something();
		testSomething.setCount(1);
		ObjFields testObjFields =  new ObjFields();
		testObjFields.setTotal(1);
		testObjFields.setName("String");
		testObjFields.setDucks(1);
		testSomething.setObjFields(testObjFields);
		List<ObjFields>testObjFieldsList = new ArrayList<>();
		testObjFieldsList.add(testObjFields);
		testSomething.setObjFieldsList(testObjFieldsList);


        // Obtain a Parcel object and write the parcelable object to it:
        Parcel parcel = Parcel.obtain();
        testSomething.writeToParcel(parcel,0);

        // After you're done with writing, you need to reset the parcel for reading:
        parcel.setDataPosition(0);

        // Reconstruct object from parcel and asserts:
        Something createdFromParcel = Something.CREATOR.createFromParcel(parcel);

        Assert.assertEquals(testSomething, createdFromParcel);
    }
}
