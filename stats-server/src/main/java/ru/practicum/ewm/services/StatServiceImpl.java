package ru.practicum.ewm.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exceptions.UriParamDecodingException;
import ru.practicum.ewm.entities.View;
import ru.practicum.ewm.dtos.ViewInDto;
import ru.practicum.ewm.dtos.ViewOutDto;
import ru.practicum.ewm.mappers.ViewMapper;
import ru.practicum.ewm.repositories.StatRepository;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatServiceImpl implements StatService {
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StatRepository statRepository;


    @Override
    public void saveView(ViewInDto viewInDto) {
        View view = statRepository.save(ViewMapper.toView(viewInDto));
        log.info("добавлен новый просмотр: id={}, uri={}", view.getId(), view.getUri());
    }

    @Override
    public List<ViewOutDto> getStats(String start, String end, String[] uris, boolean unique) {
        if (uris == null) return new ArrayList<>();

        LocalDateTime finalStartDateTime = mapToLocalDateTime(start);
        LocalDateTime finalEndDateTime = mapToLocalDateTime(end);

        List<ViewOutDto> views = Arrays.stream(uris)
                .map(uri -> statRepository.getViewWithHits(finalStartDateTime, finalEndDateTime, uri, unique))
                .flatMap(Collection::stream)
                .map(ViewMapper::toViewOut)
                .collect(Collectors.toList());

        log.info("запрос статистики обработанных просмотров, размер списка просмотров={}", views.size());
        return views;
    }

    private LocalDateTime mapToLocalDateTime(String encoded) {
        if (encoded == null) return null;

        String decoded;
        try {
            decoded = URLDecoder.decode(encoded, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            log.error("не удалось декодировать следующий параметр: {}", encoded);
            throw new UriParamDecodingException("проблема с декодированием параметра DateTime");
        }
        log.debug("завершено декодирование параметра DateTime");

        return LocalDateTime.parse(decoded, DATE_TIME);
    }
}
