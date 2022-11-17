package ru.practicum.ewm.mappers;

import ru.practicum.ewm.entities.View;
import ru.practicum.ewm.entities.ViewWithHits;
import ru.practicum.ewm.dtos.ViewInDto;
import ru.practicum.ewm.dtos.ViewOutDto;

public class ViewMapper {

    public static View toView(ViewInDto view) {
        return View.builder()
                .app(view.getApp())
                .ip(view.getIp())
                .uri(view.getUri())
                .timestamp(view.getTimestamp())
                .build();
    }

    public static ViewOutDto toViewOut(ViewWithHits view) {
        return ViewOutDto.builder()
                .app(view.getApp())
                .uri(view.getUri())
                .hits(view.getHits())
                .build();
    }
}
