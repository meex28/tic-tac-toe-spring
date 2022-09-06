package com.example.tictactoespring.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
public class User implements Serializable {
    @Id
    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private Date lastActivity;

    public User(){}

    public User(String token, String nickname, Date lastActivity) {
        this.token = token;
        this.nickname = nickname;
        this.lastActivity = lastActivity;
    }

    @Override
    public String toString() {
        return "User{" +
                "token='" + token + '\'' +
                ", nickname='" + nickname + '\'' +
                ", lastActivity=" + lastActivity +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(token, user.token);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Date getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(Date created) {
        this.lastActivity = created;
    }


}
