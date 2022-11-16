package ru.practicum.ewm.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.enums.State;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@Entity
@Table(name = "events")
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;
    @Column
    private String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;
    @Column
    private String title;
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    @Column
    private String description;
    @Column(name = "event_date")
    private LocalDateTime eventDate;
    @Column
    private boolean paid;
    @Column(name = "participant_limit")
    private int participantLimit;
    @Column(name = "published_on")
    private LocalDateTime publishedOn;
    @Enumerated(EnumType.STRING)
    @Column
    private State state;
    @Column(name = "location_latitude")
    private float locationLatitude;
    @Column(name = "location_longitude")
    private float locationLongitude;
    @Column(name = "request_moderation")
    private boolean requestModeration;
}
