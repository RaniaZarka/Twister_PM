

        package com.example.twisterpm; ;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Comments implements Serializable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("messageId")
    @Expose
    private Integer messageId;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("user")
    @Expose
    private String user;
    private final static long serialVersionUID = -880274776449430983L;

    /**
     * No args constructor for use in serialization
     *
     */
    public Comments() {
    }

    /**
     *
     * @param messageId
     * @param id
     * @param user
     * @param content
     */
    public Comments(Integer id, Integer messageId, String content, String user) {
        super();
        this.id = id;
        this.messageId = messageId;
        this.content = content;
        this.user = user;
    }

    public Comments( Integer messageId, String content, String user) {
        this.messageId = messageId;
        this.content = content;
        this.user = user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
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

    @Override
    public String toString() {
        return  id + ": " + user + " " + content + ", " + messageId;}


}