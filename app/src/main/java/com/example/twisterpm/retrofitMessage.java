package com.example.twisterpm;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class retrofitMessage {
    private static Retrofit retrofit =null;

    public static Retrofit getComments(String baseUrl){
        if(retrofit ==null){
            retrofit= new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
