package com.example.frontend.entity.chat;

import com.stfalcon.chatkit.commons.models.IUser;


/**
 * 会话的参与者
 */
public class User implements IUser {

    private String id;
    private String name;
    private String avatar;  // 头像
    private boolean online;

    private String account; // 对方用户名
    private String type;   //用户类型 S/T
    private String userId; // 对方id

    public User(String id, String name, String avatar, boolean online) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.online = online;
    }

    public User(String id, String name, String avatar, String account, String type, String userId) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.account = account;
        this.type = type;
        this.userId = userId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAvatar() {
        return avatar;
    }

    public boolean isOnline() {
        return online;
    }

    public String getUserId() {
        return userId;
    }
}
