package com.openu.a2017_app1.data.parse;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.openu.a2017_app1.data.Converter;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

/**
 * Created by Raz on 30/12/2016.
 */

/* package */ class BitmapConverter implements Converter {
    @Override
    public Object convert(Object value) {
        Bitmap bitmap = (Bitmap) value;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bytes = stream.toByteArray();

        ParseFile file = new ParseFile(createUniqueFileName(), bytes);
        try {
            file.save();
        } catch (ParseException e) {
            return null;
        }

        return file;
    }

    @Override
    public boolean canConvert(Object value) {
        return value instanceof Bitmap;
    }

    @Override
    public Object convertBack(Object obj) {
        ParseFile file = (ParseFile) obj;
        byte[] bytes;
        try {
            bytes = file.getData();
        } catch (ParseException e) {
            return null;
        }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    @Override
    public boolean canConvertBack(Object obj) {
        return obj instanceof ParseFile && ((ParseFile) obj).getName().endsWith(".bmp");
    }

    /**
     * Creates an unique file name for the bitmap.
     * @return unique file name.
     */
    private String createUniqueFileName() {
        return UUID.randomUUID().toString() + ".bmp";
    }
}
