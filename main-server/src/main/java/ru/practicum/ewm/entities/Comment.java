package ru.practicum.ewm.entities;

import lombok.*;
import ru.practicum.ewm.enums.State;
import ru.practicum.ewm.enums.Status;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;
    private String text;
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;
    @Column(name = "created")
    private LocalDateTime created;
    @Enumerated(EnumType.STRING)
    @Column
    private State state;
}
