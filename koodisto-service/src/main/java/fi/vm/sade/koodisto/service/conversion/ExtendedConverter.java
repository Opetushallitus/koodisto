package fi.vm.sade.koodisto.service.conversion;

import org.springframework.core.convert.converter.Converter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface ExtendedConverter<S, T> extends Converter<S, T> {

    public default <A extends List<S>, B extends List<T>> List<T> convertAll(A source) {
        List<T> list = new ArrayList<>();
        for (S e : source) {
            list.add(this.convert(e));
        }
        return list;
    }


    public default <A extends Set<S>, B extends Set<T>> Set<T> convertAll(A source) {
        Set<T> set = new HashSet<>();
        for (S e : source) {
            set.add(this.convert(e));
        }
        return set;
    }
}
