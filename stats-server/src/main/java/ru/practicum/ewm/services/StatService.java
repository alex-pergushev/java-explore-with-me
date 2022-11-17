package ru.practicum.ewm.services;

import ru.practicum.ewm.dtos.ViewInDto;
import ru.practicum.ewm.dtos.ViewOutDto;

import java.util.List;

public interface StatService {

    void saveView(ViewInDto viewInDto);

    List<ViewOutDto> getStats(String start, String end, String[] uris, boolean unique);
}
