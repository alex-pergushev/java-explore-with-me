package ru.practicum.ewm.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dtos.UserInDto;
import ru.practicum.ewm.dtos.UserOutDto;
import ru.practicum.ewm.exceptions.UserNotFoundException;
import ru.practicum.ewm.entities.QUser;
import ru.practicum.ewm.entities.User;
import ru.practicum.ewm.mappers.UserMapper;
import ru.practicum.ewm.repositories.UserRepository;
import ru.practicum.ewm.utils.Pagination;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserOutDto> getUsers(int[] ids, int from, int size) {
        Pageable pageable = Pagination.of(from, size);
        List<User> users;

        if (ids != null) {
            QUser user = QUser.user;
            List<Long> idsLong = Arrays.stream(ids).mapToObj(Long::valueOf).collect(Collectors.toList());
            users = userRepository.findAll(user.id.in(idsLong), pageable).getContent();
        } else users = userRepository.findAll(pageable).getContent();

        return users.stream()
                .map(UserMapper::toUserOut)
                .collect(Collectors.toList());
    }

    @Override
    public UserOutDto createUser(UserInDto userInDto) {
        User saved = userRepository.save(UserMapper.toUser(userInDto));
        log.info("новый пользователь id={} успешно добавлен", saved.getId());
        return UserMapper.toUserOut(saved);
    }

    @Override
    public void deleteUser(long userId) {
        if (!userRepository.existsById(userId))
            throw new UserNotFoundException(String.format("пользователь с id=%s не найден", userId));

        userRepository.deleteById(userId);
        log.info("пользователь id={} удален", userId);
    }
}
