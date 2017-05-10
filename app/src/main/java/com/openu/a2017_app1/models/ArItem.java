package com.openu.a2017_app1.models;

import android.graphics.Bitmap;

import com.openu.a2017_app1.data.QueryBuilder;

import static com.openu.a2017_app1.models.Place.FIELD_NAME;

/**
 * Created by Raz on 07/05/2017.
 */

public class ArItem extends Model {

    public static final String FIELD_LOCATION = "location";
    public static final String FIELD_STICKER = "sticker";
    public static final String FIELD_TEXT = "text";
    public static final String FIELD_ORIENTATION = "orientation";

    /**
     * default constructor
     */
    public ArItem()
    {

    }

    public LocationPoint getLocation() { return (LocationPoint)this.getAttribute(FIELD_LOCATION); }

    public void setLocation(LocationPoint location)
    {
        this.setAttribute(FIELD_LOCATION, location);
    }

    public int getSticker()
    {
        return (int) this.getAttribute(FIELD_STICKER);
    }

    public void setSticker(int sticker) { this.setAttribute(FIELD_STICKER, sticker); }

    public String getText()
    {
        return (String) this.getAttribute(FIELD_TEXT);
    }

    public void setText(String text) { this.setAttribute(FIELD_TEXT, text); }

    public float getOrientation()
    {
        Object value = this.getAttribute(FIELD_ORIENTATION);
        if (value instanceof Double) {
            return ((Double)value).floatValue();
        } else if (value instanceof Float) {
            return ((Float)value).floatValue();
        }
        return 0;
    }

    public void setOrientation(float orientation) { this.setAttribute(FIELD_ORIENTATION, orientation); }
}