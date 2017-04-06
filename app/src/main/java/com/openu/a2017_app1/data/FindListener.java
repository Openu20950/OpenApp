package com.openu.a2017_app1.data;

import java.util.List;

/**
 * Created by User on 30/03/2017.
 */
public interface FindListener<T> {
    /**
     * Callback that called when find operation has finised.
     * @param item the item if it found, or null otherwise.
     */
    void onItemFound(T item);
}
