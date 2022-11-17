package ru.practicum.ewm.mappers;

import ru.practicum.ewm.dtos.EventFullOutDto;
import ru.practicum.ewm.dtos.EventInDto;
import ru.practicum.ewm.dtos.EventShortOutDto;
import ru.practicum.ewm.dtos.LocationDto;
import ru.practicum.ewm.entities.Category;
import ru.practicum.ewm.entities.Event;
import ru.practicum.ewm.entities.User;
import ru.practicum.ewm.enums.State;

import java.time.LocalDateTime;

public class EventMapper {
    public static EventShortOutDto toEventShort(Event event, int confirmedRequests, long views) {
        return EventShortOutDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .eventDate(event.getEventDate())
                .paid(event.isPaid())
                .title(event.getTitle())
                .initiator(UserMapper.toUserShort(event.getInitiator()))
                .category(CategoryMapper.toCategoryOut(event.getCategory()))
                .confirmedRequests(confirmedRequests)
                .views(views)
                .build();
    }

    public static EventFullOutDto toEventFull(Event event, int confirmedRequests, long views) {
        return EventFullOutDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .eventDate(event.getEventDate())
                .paid(event.isPaid())
                .title(event.getTitle())
                .initiator(UserMapper.toUserShort(event.getInitiator()))
                .category(CategoryMapper.toCategoryOut(event.getCategory()))
                .confirmedRequests(confirmedRequests)
                .views(views)
                .location(getLocationFromEvent(event))
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.isRequestModeration())
                .state(event.getState())
                .build();
    }

    public static Event toEvent(EventInDto eventInDto, Category category, User user) {
        return Event.builder()
                .category(category)
                .eventDate(eventInDto.getEventDate())
                .annotation(eventInDto.getAnnotation())
                .createdOn(LocalDateTime.now())
                .initiator(user)
                .description(eventInDto.getDescription())
                .participantLimit(eventInDto.getParticipantLimit())
                .locationLatitude(eventInDto.getLocation().getLat())
                .locationLongitude(eventInDto.getLocation().getLon())
                .state(State.PENDING)
                .title(eventInDto.getTitle())
                .requestModeration(eventInDto.isRequestModeration())
                .paid(eventInDto.isPaid())
                .build();
    }

    private static LocationDto getLocationFromEvent(Event event) {
        return LocationDto.builder()
                .lat(event.getLocationLatitude())
                .lon(event.getLocationLongitude())
                .build();
    }
}
