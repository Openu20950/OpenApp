package com.openu.a2017_app1.data.parse;

import com.openu.a2017_app1.data.Converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Raz on 30/12/2016.
 */

public class Converters {

    private static Converters sInstance;

    private List<Converter> converters;

    private Converters()
    {
        initializeConverters();
    }

    private void initializeConverters() {
        converters = new ArrayList<>();
        converters.add(new ModelConverter());
        converters.add(new BitmapConverter());
        converters.add(new ListConverter(this));
    }

    public static Converters getInstance() {
        if (sInstance == null) {
            synchronized (Converters.class) {
                if (sInstance == null) {
                    sInstance = new Converters();
                }
            }
        }
        return sInstance;
    }

    /**
     * Converts the value to savable object. If no conversion is needed, the original value is returned.
     * @param value The value to convert.
     * @return the converted value, or the original value, if no conversion is needed.
     */
    public Object convert(Object value) {
        for (Converter conv: this.converters) {
            if (conv.canConvert(value)) {
                return conv.convert(value);
            }
        }
        return value;
    }

    /**
     * Converts the object to value. If no conversion is needed, the original value is returned.
     * @param obj The object convert.
     * @return the converted object, or the original object, if no conversion is needed.
     */
    public Object convertBack(Object obj) {
        for (Converter conv: this.converters) {
            if (conv.canConvertBack(obj)) {
                return conv.convertBack(obj);
            }
        }
        return obj;
    }
}
