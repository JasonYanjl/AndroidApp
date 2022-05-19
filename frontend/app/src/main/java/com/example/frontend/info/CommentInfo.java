package com.example.frontend.info;

import com.example.frontend.SearchActivity;

public class CommentInfo {
    public Integer userid, commentid, avatarid;
    public String username, Text, Time, Intro;

    public CommentInfo(Integer userid, String username, Integer avatarid, String Intro,
                       Integer commentid, String Text, String Time) {
        this.userid = userid;
        this.username = username;
        this.avatarid = avatarid;
        this.Intro = Intro;
        this.commentid = commentid;
        this.Text = Text;
        this.Time = Time;
    }
}
