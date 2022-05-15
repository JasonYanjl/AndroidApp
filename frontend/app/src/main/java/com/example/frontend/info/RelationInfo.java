package com.example.frontend.info;

public class RelationInfo {
    public String username, avatarFilename, intro;
    public Integer userid, avatarid, isSubscribe, isBlock;

    public RelationInfo(Integer userid, String username, Integer avatarid, String avatarFilename,
                        String intro, Integer isSubscribe, Integer isBlock) {
        this.userid = userid;
        this.username = username;
        this.avatarid = avatarid;
        this.avatarFilename = avatarFilename;
        this.intro = intro;
        this.isSubscribe = isSubscribe;
        this.isBlock = isBlock;
    }
}
