package com.zb.cinema.domain.movie.entity;

import com.zb.cinema.domain.movie.type.MovieStatus;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;
import lombok.Setter;
import lombok.ToString;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Movie {

    @Id
    private long code;

    @Column
    private String title;

    @Column
    private String actors;

    @Column
    private String directors;

    @Column
    private String genre;

    @Column
    private String nation;

    @Column
    private long runTime;

    @Column
    private LocalDateTime openDt;

    @Column
    @Enumerated(EnumType.STRING)
    private MovieStatus status;

}
