package com.reubbishsecurity.CovidVaccinations.forum.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "threads")
public class Thread {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 255)
    private String title;

    private Date date;

    private Date latestPost;

    private long replies;

    @OneToMany(mappedBy = "thread", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Post> posts;

    public String getShortDate() {
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }

    public String getFormattedLatestPost() {
        return new SimpleDateFormat("dd-MM-yyyy HH:mm").format(latestPost);
    }

    public void incrementReplies() {
        replies += 1;
    }

    public Thread() {}

    public Thread(String title, Date date) {
        this.title = title;
        this.date = date;
        this.latestPost = date;
        this.replies = 0;
    }

    public int compareTo(Thread u) {
        if (date == null || u.getDate() == null) {
            return 0;
        }
        return date.compareTo(u.getDate());
    }

    @Override
    public String toString() {
        return "Thread{" +
                "title='" + title + '\'' +
                ", date=" + date +
                '}';
    }
}
