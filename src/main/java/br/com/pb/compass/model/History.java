package br.com.pb.compass.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "history")
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date date;
    private State state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;


    public History(Date date, State state) {
        this.date = date;
        this.state = state;
    }
    public History(Date date, State state, Post post){
        this.date = date;
        this.state = state;
        this.post = post;
    }

    protected History() {

    }
}
