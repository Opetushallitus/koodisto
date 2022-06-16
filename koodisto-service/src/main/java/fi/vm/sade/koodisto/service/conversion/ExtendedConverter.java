package fi.vm.sade.koodisto.service.conversion;

import org.springframework.core.convert.converter.Converter;

import java.util.*;
import java.util.stream.Collectors;

public interface ExtendedConverter<S, T> extends Converter<S, T> {

    default <A extends List<S>> List<T> convertAll(A source) {
        return source.stream().map(this::convert).collect(Collectors.toList());
    }

}
