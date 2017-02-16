package com.openu.a2017_app1.data;

/**
 * Created by Raz on 16/02/2017.
 */

import com.openu.a2017_app1.models.IModel;
import com.parse.ParseObject;

/**
 * Converts a model to the database
 */
/* package */ class ModelConverter implements Converter {

    private static final String MODELS_PACKAGE = "com.openu.a2017_app1.models.";

    @Override
    public Object convert(Object value) {
        return ModelCreator.createParseFromModel((IModel)value);
    }

    @Override
    public boolean canConvert(Object value) {
        return value instanceof IModel;
    }

    @Override
    public Object convertBack(Object obj) {
        ParseObject parse = (ParseObject)obj;
        try {
            return ModelCreator.createModelFromParse(parse, (Class<? extends IModel>) Class.forName(MODELS_PACKAGE + parse.getClassName()));
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    @Override
    public boolean canConvertBack(Object obj) {
        return obj instanceof ParseObject;
    }
}
