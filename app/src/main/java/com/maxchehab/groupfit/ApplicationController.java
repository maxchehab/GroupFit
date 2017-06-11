package com.maxchehab.groupfit;


import android.content.Context;

public final class ApplicationController {

    public static Context CONTEXT;

    private ApplicationController () { // private constructor
        CONTEXT = null;
    }
    public static void set(Context context) {
        CONTEXT = context;
    }





}