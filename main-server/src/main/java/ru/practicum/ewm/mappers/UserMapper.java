package ru.practicum.ewm.mappers;

import ru.practicum.ewm.entities.User;
import ru.practicum.ewm.dtos.UserInDto;
import ru.practicum.ewm.dtos.UserOutDto;
import ru.practicum.ewm.dtos.UserShortOutDto;

public class UserMapper {
    public static UserShortOutDto toUserShort(User user) {
        return UserShortOutDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public static UserOutDto toUserOut(User user) {
        return UserOutDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User toUser(UserInDto user) {
        return User.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
