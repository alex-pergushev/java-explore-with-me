package ru.practicum.ewm.enums.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.exceptions.InvalidRequestException;
import ru.practicum.ewm.enums.SortingEvents;

@Component
public class StringToSortConverter implements Converter<String, SortingEvents> {

    @Override
    public SortingEvents convert(String source) {
        try {
            return SortingEvents.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException(String.format("произошла непредвиденная ошибка при преобразовании " +
                    "строкового значения=%s в SortingEvents", source));
        }
    }
}
