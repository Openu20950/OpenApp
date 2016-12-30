package com.openu.a2017_app1.data;

/**
 * Created by Raz on 30/12/2016.
 *
 * Converts types before saving and loading from database
 */
public interface Converter {
    /**
     * Convert the value to savable object.
     * @param value the value to convert.
     * @return the object to save.
     */
    public Object convert(Object value);

    /**
     * Check if the value is convertible with this converter.
     * @param value the value to convert.
     * @return true, if the value is convertible. false, otherwise.
     */
    public boolean canConvert(Object value);

    /**
     * Converts back the object to the value.
     * @param obj the object.
     * @return the value.
     */
    public Object convertBack(Object obj);

    /**
     * Check if the object is convertible back with this converter.
     * @param obj the object to convert.
     * @return true, if the object is convertible. false, otherwise.
     */
    public boolean canConvertBack(Object obj);

}
