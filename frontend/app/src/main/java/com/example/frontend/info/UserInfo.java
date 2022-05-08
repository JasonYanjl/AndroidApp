package com.example.frontend.info;

public class UserInfo {
    Integer userid;
    String username;
    Integer avatarid;
    String intro;
    String jwt;

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
}
