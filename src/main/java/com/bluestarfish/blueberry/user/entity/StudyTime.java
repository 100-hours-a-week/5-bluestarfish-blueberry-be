package com.bluestarfish.blueberry.user.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.sql.Time;
import java.util.Date;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "study_time", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "date"})
})
public class StudyTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Date date;

    @Column(nullable = false)
    @ColumnDefault("00:00:00")
    private Time time;

    @Builder
    public StudyTime(
            User user,
            Date date
    ) {
        this.user = user;
        this.date = date;
        this.time = Time.valueOf("00:00:00");
    }

}
