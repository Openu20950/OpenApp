package com.openu.a2017_app1.data.parse;

import com.openu.a2017_app1.data.Converter;
import com.openu.a2017_app1.models.Model;

/**
 * Created by Raz on 30/12/2016.
 *
 * Converts a model to the database. This class use lazy loading for better performance.
 */
/* package */ class ModelConverter implements Converter {
    @Override
    public Object convert(Object value) {
        Model model = (Model) value;
        model.save();
        return model.getAttribute(model.getPrimaryKey());
    }

    @Override
    public boolean canConvert(Object value) {
        return value instanceof Model;
    }

    @Override
    public Object convertBack(Object obj) {
        return null;
    }

    @Override
    public boolean canConvertBack(Object obj) {
        return false;
    }
}