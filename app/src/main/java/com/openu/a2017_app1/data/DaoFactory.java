package com.openu.a2017_app1.data;

/**
 * Created by Raz on 25/12/2016.
 */

public class DaoFactory {
    private static DaoFactory sInstance;

    private Class<? extends Dao> daoClass;

    /**
     * Initialize a new instance of DaoFactory class
     */
    private DaoFactory() { }

    /**
     * Get the singleton instance of DaoFactory class
     * @return
     */
    public static DaoFactory getInstance() {
        if (sInstance == null) {
            synchronized (DaoFactory.class) {
                if (sInstance == null) {
                    sInstance = new DaoFactory();
                }
            }
        }
        return sInstance;
    }

    /**
     * Set the default Dao type
     * @param dao
     */
    public void setDefault(Class<? extends Dao> dao) {
        daoClass = dao;
    }

    /**
     * Creates a new instance of the Dao class
     * @return
     */
    public Dao create() {
        try {
            return daoClass.newInstance();
        } catch (Exception e) {
            return null;
        }
    }


}
