package ru.practicum.ewm.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.enums.Status;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "request_event")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_event_id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
    private LocalDateTime created;
    @ManyToOne
    @JoinColumn(name = "requester")
    private User requester;
    @Enumerated(EnumType.STRING)
    private Status status;
}