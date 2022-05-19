package com.example.frontend.info;

public class CommentInfo {
    Integer userid, commmentid;
    String username, Text, Time;

    public CommentInfo(Integer userid, String username, Integer commmentid, String Text, String Time) {
        this.userid = userid;
        this.username = username;
        this.commmentid = commmentid;
        this.Text = Text;
        this.Time = Time;
    }
}
