package com.example.twisterpm;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;



import java.io.Serializable;

public class Message implements Serializable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("user")
    @Expose
    private String user;
    @SerializedName("totalComments")
    @Expose
    private Integer totalComments;

    private static int idCounter =0;
    private static int numberComent=0;
    private final static long serialVersionUID = -5946129236403107348L;

    /**
     * No args constructor for use in serialization
     *
     */
    public Message() {


    }

    /**
     *
     * @param totalComments
     * @param id
     * @param user
     * @param content
     */
    public Message(Integer id, String content, String user, Integer totalComments) {
        super();
        this.id = id;
        this.content = content;
        this.user = user;
        this.totalComments = totalComments;
    }
    public Message(String content, String user)
    {
        this.content = content;
        this.user = user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Integer getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(Integer totalComments) {
        this.totalComments = totalComments;
    }

    public String toString() {return id + ", " + user + ", " + content + ", " + totalComments;}

}