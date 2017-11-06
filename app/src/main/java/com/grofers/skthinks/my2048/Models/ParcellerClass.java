package com.grofers.skthinks.my2048.Models;

import com.example.AutoParcel;

import org.parceler.Parcel;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Skthinks on 03/08/17.
 */
@Parcel
@AutoParcel(isParcel = true)
@Setter
@Getter
public class ParcellerClass {

    private static int MONKEY = 0;

    int a;

    int b;
}
