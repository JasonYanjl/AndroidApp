package com.example.frontend.entity.chat;

import com.stfalcon.chatkit.commons.models.IDialog;

import java.util.ArrayList;


/**
 * 会话列表的一项
 */
public class Dialog implements IDialog<Message>, Comparable<Dialog> {

    public ArrayList<User> users; // 参与用户，只需对方
    private String id;
    private String dialogPhoto; // 头像
    private String dialogName;  // 名称
    private Message lastMessage; // 最后一条消息

    private int unreadCount; // 未读数量

    public Dialog(String id, String name, String photo,
                  ArrayList<User> users, Message lastMessage, int unreadCount) {

        this.id = id;
        this.dialogName = name;
        this.dialogPhoto = photo;
        this.users = users;
        this.lastMessage = lastMessage;
        this.unreadCount = unreadCount;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDialogPhoto() {
        return dialogPhoto;
    }

    @Override
    public String getDialogName() {
        return dialogName;
    }

    @Override
    public ArrayList<User> getUsers() {
        return users;
    }

    @Override
    public Message getLastMessage() {
        return lastMessage;
    }

    @Override
    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    @Override
    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }


    @Override
    public int compareTo(Dialog o) {
        return o.getLastMessage().getCreatedAt().compareTo(this.getLastMessage().getCreatedAt());
    }
}
