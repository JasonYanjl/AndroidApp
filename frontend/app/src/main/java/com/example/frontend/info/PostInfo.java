package com.example.frontend.info;

import org.w3c.dom.Comment;

import java.util.LinkedList;

public class PostInfo {
    public String username, avatarFilename, filename,intro;
    public Integer userid, avatarid, isSubscribe, isBlock, postid, fileid, Type;

    public String Title, Text, Location, Time;

    public Boolean catchLike, catchComment;
    public LinkedList<LikeInfo> Like;
    public LinkedList<CommentInfo> Comment;


    public PostInfo(Integer postid, Integer userid, String username, Integer avatarid, String avatarFilename,
                    String intro, Integer fileid, String filename, String Title, String Text, Integer Type,
                         String Time, String Location, Integer isSubscribe, Integer isBlock) {
        this.postid = postid;
        this.userid = userid;
        this.username = username;
        this.avatarid = avatarid;
        this.avatarFilename = avatarFilename;
        this.intro = intro;
        this.fileid = fileid;
        this.filename = filename;
        this.Title = Title;
        this.Text = Text;
        this.Type = Type;
        this.Time = Time;
        this.Location = Location;
        this.isSubscribe = isSubscribe;
        this.isBlock = isBlock;
        this.Like = new LinkedList<LikeInfo>();
        this.Comment = new LinkedList<CommentInfo>();
        this.catchLike = false;
        this.catchComment = false;
    }

    public String LikeList2String(){
        String ret = " ";
        for(int i = 0; i < Like.size(); i++) {
            if (ret.equals(" ")) ret = ret.concat(Like.get(i).username);
            else ret = ret.concat(", " + Like.get(i).username);
        }
        return ret;
    }
}
