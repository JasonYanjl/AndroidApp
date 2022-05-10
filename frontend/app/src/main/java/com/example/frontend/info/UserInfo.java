package com.example.frontend.info;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.frontend.entity.chat.Message;

public class UserInfo {
    Integer userid;
    String username;
    Integer avatarid;
    String intro;
    String jwt;
    public List<Message> messageList = Collections.synchronizedList(new ArrayList<>());

    private static volatile UserInfo userInfo;
    private UserInfo() {

    }
    public static UserInfo getInstance() {
        UserInfo info = userInfo;
        if (info == null) {
            synchronized (UserInfo.class) {
                info = userInfo;
                if (info == null) {
                    info = new UserInfo();
                    userInfo = info;
                }
            }
        }
        return info;
    }

    public Integer getUserid() {
        return userid;
    }

    public String getUsername() { return username; }

    public Integer getAvatarid() { return avatarid; }

    public String getIntro() {
        return intro;
    }

    public String getJwt() {return jwt;}

    public void setUserid(Integer userid) { this.userid = userid; }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAvatarid(Integer avatarid) { this.avatarid = avatarid; }

    public void setUserInfo(String intro) {
        this.intro = intro;
    }

    public void setJwt(String jwt) {this.jwt = jwt;}

    public List<Message> getMessageList() {return messageList; }

    public void addMessage(Message nowMessage) {
        this.messageList.add(nowMessage);
    }

    public void clearArray() {
        this.messageList.clear();
    }
}
