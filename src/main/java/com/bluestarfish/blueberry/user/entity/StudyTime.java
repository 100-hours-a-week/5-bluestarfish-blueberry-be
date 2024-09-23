package com.bluestarfish.blueberry.user.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.sql.Time;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "study_time",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "date"})},
        indexes = {@Index(name = "idx_time", columnList = "time")}
)
public class StudyTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    private LocalDate date;

    @Column(nullable = false)
    private Time time;

    @PrePersist
    public void prePersist() {
        if (this.time == null) {
            this.time = Time.valueOf("00:00:00"); // 기본값 설정
        }

        date = LocalDate.now();
    }

    @Builder
    public StudyTime(
            User user,
            LocalDate date
    ) {
        this.user = user;
        this.date = date;
    }

}
