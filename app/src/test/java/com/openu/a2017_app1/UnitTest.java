package com.openu.a2017_app1;

import com.openu.a2017_app1.data.DaoFactory;
import com.openu.a2017_app1.data.MemoryDao;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by Raz on 09/01/2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class UnitTest {

    @BeforeClass
    public static void beforeClass() {
        DaoFactory.getInstance().setDefault(MemoryDao.class);
    }

    @After
    public void after() {
        MemoryDao.clearDb();
    }
}
