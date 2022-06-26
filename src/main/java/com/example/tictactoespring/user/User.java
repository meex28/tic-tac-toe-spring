package com.example.tictactoespring.user;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
public class User implements Serializable {
    @Getter
    @Setter
    @Id
    private String token;

    @Getter
    @Setter
    @Column(nullable = false)
    private String nickname;

    @Getter
    @Setter
    @Column(nullable = false)
    private Date created;

    public User(){}

    public User(String token, String nickname, Date created) {
        this.token = token;
        this.nickname = nickname;
        this.created = created;
    }

    @Override
    public String toString() {
        return "User{" +
                "token='" + token + '\'' +
                ", nickname='" + nickname + '\'' +
                ", created=" + created +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(token, user.token);
    }
}
