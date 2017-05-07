package com.openu.a2017_app1.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

/**
 * Created by Raz on 07/05/2017.
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
        return obj == JSONObject.NULL;
    }
}
