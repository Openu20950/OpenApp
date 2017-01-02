package com.openu.a2017_app1.data.parse;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.openu.a2017_app1.data.Converter;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Raz on 30/12/2016.
 */

/* package */ class ListConverter implements Converter {
    @Override
    public Object convert(Object value) {
        List<?> original = (List<?>) value;
        List<Object> converted = new ArrayList<>();

        Converters converters = Converters.getInstance();
        for (Object item: original) {
            converted.add(converters.convert(item));
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

        Converters converters = Converters.getInstance();
        for (Object item : original) {
            converted.add(converters.convertBack(item));
        }
        return converted;
    }

    @Override
    public boolean canConvertBack(Object obj) {
        return obj instanceof List;
    }
}
