package com.openu.a2017_app1.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raz on 30/12/2016.
 */

/* package */ class ListConverter implements Converter {

    private Converters converters;

    public ListConverter(Converters converters) {
        this.converters = converters;
    }

    @Override
    public Object convert(Object value) {
        List<?> original = (List<?>) value;
        List<Object> converted = new ArrayList<>();

        for (Object item: original) {
            converted.add(this.converters.convert(item));
        }
        return converted;
    }

    @Override
    public boolean canConvert(Object value) {
        return value instanceof List;
    }

    @Override
    public Object convertBack(Object obj) {
        List<?> original = (List<?>) obj;
        List<Object> converted = new ArrayList<>();

        for (Object item : original) {
            converted.add(this.converters.convertBack(item));
        }
        return converted;
    }

    @Override
    public boolean canConvertBack(Object obj) {
        return obj instanceof List;
    }
}
