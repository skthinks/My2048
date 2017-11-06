package com.grofers.skthinks.my2048.Models;

import com.example.GenerateTest;

import org.parceler.Parcel;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Skthinks on 03/08/17.
 */
@Parcel
@GenerateTest(usesParcellerAnnotation = true)
@Setter
@Getter
public class ParcellerClass {

    private static int MONKEY = 0;

    transient int joker;

    final int donkey = 0;

    static final int octopus = -1;

    int a;

    int b;
}
