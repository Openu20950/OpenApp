package com.openu.a2017_app1.data;

import java.util.List;

/**
 * Created by User on 25/03/2017.
 */
public interface GetAllListener<T> {
    void onItemsReceived(List<T> items);
}
