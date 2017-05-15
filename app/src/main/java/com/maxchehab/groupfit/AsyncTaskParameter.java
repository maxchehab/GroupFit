package com.maxchehab.groupfit;

/**
 * Created by maxchehab on 5/11/2017.
 */

public class AsyncTaskParameter {

    public String input;
    public StreamRequestListener callback;


    public AsyncTaskParameter(String input, StreamRequestListener callback){
        this.input = input;
        this.callback = callback;
    }
}
