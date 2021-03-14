package com.example.twisterpm;

public class ApiUtils {

    private static final String BASE_URL= "https://anbo-restmessages.azurewebsites.net/api/";

    public static ApiServices getMessagesService(){
        return retrofitMessage.getComments(BASE_URL).create(ApiServices.class);
    }




}
