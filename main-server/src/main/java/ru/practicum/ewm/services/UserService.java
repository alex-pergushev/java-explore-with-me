package ru.practicum.ewm.services;

import ru.practicum.ewm.dtos.UserInDto;
import ru.practicum.ewm.dtos.UserOutDto;

import java.util.List;

public interface UserService {

    List<UserOutDto> getUsers(int[] ids, int from, int size);

    UserOutDto createUser(UserInDto userInDto);

    void deleteUser(long userId);
}