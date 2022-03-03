package com.reubbishsecurity.CovidVaccinations.forum.entity;

import com.reubbishsecurity.CovidVaccinations.authentication.entity.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "thread_id", nullable = false)
    private Thread thread;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Date date;

    @Column(length = 5000)
    private String content;

    public String getFullDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        return dateFormat.format(date);
    }

    public Post() {}

    public Post(Thread thread, User user, Date date, String content) {
        this.thread = thread;
        this.user = user;
        this.date = date;
        this.content = content;
    }

    public int compareTo(Post u) {
        if (date == null || u.getDate() == null) {
            return 0;
        }
        return date.compareTo(u.getDate());
    }

    @Override
    public String toString() {
        return "Post{" +
                "thread=" + thread +
                ", user=" + user +
                ", date=" + date +
                ", content='" + content + '\'' +
                '}';
    }
}
