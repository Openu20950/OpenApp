package com.openu.a2017_app1.data;

import com.openu.a2017_app1.data.Converter;
import com.openu.a2017_app1.models.LocationPoint;
import com.parse.ParseGeoPoint;

/**
 * Created by Raz on 1/1/2017.
 */

/* package */ class LocationConverter implements Converter {
    @Override
    public Object convert(Object value) {
        LocationPoint point = (LocationPoint) value;
        ParseGeoPoint parseGeoPoint = new ParseGeoPoint(point.getLatitude(), point.getLongitude());
        return parseGeoPoint;
    }

    @Override
    public boolean canConvert(Object value) {
        return value instanceof LocationPoint;
    }

    @Override
    public Object convertBack(Object obj) {
        ParseGeoPoint parseGeoPoint = (ParseGeoPoint) obj;
        LocationPoint point = new LocationPoint();
        point.setLatitude(parseGeoPoint.getLatitude());
        point.setLongitude(parseGeoPoint.getLongitude());
        return point;
    }

    @Override
    public boolean canConvertBack(Object obj) {
        return obj instanceof ParseGeoPoint;
    }
}
