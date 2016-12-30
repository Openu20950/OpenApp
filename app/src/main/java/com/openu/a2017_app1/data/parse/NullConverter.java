package com.openu.a2017_app1.data.parse;

import com.openu.a2017_app1.data.Converter;

import org.json.JSONObject;

/**
 * Created by Raz on 30/12/2016.
 */

/* package */ class NullConverter implements Converter {
    @Override
    public Object convert(Object value) {
        return JSONObject.NULL;
    }

    @Override
    public boolean canConvert(Object value) {
        return value == null;
    }

    @Override
    public Object convertBack(Object obj) {
        return null;
    }

    @Override
    public boolean canConvertBack(Object obj) {
        return JSONObject.NULL.equals(obj);
    }
}
