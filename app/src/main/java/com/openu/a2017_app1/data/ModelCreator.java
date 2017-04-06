package com.openu.a2017_app1.data;

import com.openu.a2017_app1.models.IModel;
import com.openu.a2017_app1.models.Model;
import com.parse.ParseObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Raz on 16/02/2017.
 *
 * Creates a model from ParseObject
 */

/* package */ class ModelCreator {

    /* package */ static final String CREATED_AT_ATTRIBUTE = "created_at";
    /* package */ static final String UPDATED_AT_ATTRIBUTE = "updated_at";

    public static IModel createModelFromParse(ParseObject obj, Class<? extends IModel> modelClass) {
        try {
            IModel model = modelClass.newInstance();
            fillModel(model, obj);
            return model;
        } catch (Exception e) {
            return null;
        }
    }

    public static ParseObject createParseFromModel(IModel model) {
        ParseObject object = ParseDao.getParseObject(model);
        Converters converters = Converters.getInstance();
        for (Map.Entry<String, Object> attribute : model.getAttributes().entrySet()) {
            if (attribute.getKey().startsWith("_")) {
                continue;
            }
            if (attribute.getKey().equals(model.getPrimaryKey())) {
                object.setObjectId((String)attribute.getValue());
            } else {
                object.put(attribute.getKey(), converters.convert(attribute.getValue()));
            }
        }
        return object;
    }

    private static void fillModel(IModel model, ParseObject obj) {
        Map<String, Object> attributes = getAttributes(model, obj);
        attributes.put(model.getPrimaryKey(), obj.getObjectId());
        attributes.put(ParseDao.PARSE_OBJECT_ATTRIBUTE, obj);
        attributes.put(CREATED_AT_ATTRIBUTE, obj.getCreatedAt());
        attributes.put(UPDATED_AT_ATTRIBUTE, obj.getUpdatedAt());
        model.fill(attributes);
    }

    private static Map<String,Object> getAttributes(IModel model, ParseObject obj) {
        Map<String, Object> res = new HashMap<>();
        Object value;
        Converters converters = Converters.getInstance();
        for (String key : obj.keySet()) {
            value = obj.get(key);
            res.put(key, converters.convertBack(value));
        }
        return res;
    }
}
