package com.example.twisterpm;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiServices {

    @GET("messages")
    Call<List<Message>> getAllMessages();

    @GET("messages/{messageId}/comments")
    Call<List<Comments>>getCommentById(@Path("messageId") int messageId);

    @POST("messages/{messageId}/comments")
    Call<Comments>saveCommentBody(@Body Comments comment);

    @POST("messages")
    @FormUrlEncoded
    Call<Message> saveMessageBody(@Field("Content") String content, @Field("User") String user);

    @POST("messages")
    Call<Message>saveMessage(@Body Message message);

    @DELETE("messages/{id}")
    Call<Message> deleteMessage(@Path("id") int id);

    @DELETE("messages/{id}")
    Call<Message> deleteComment(@Path("id") int id);




}
