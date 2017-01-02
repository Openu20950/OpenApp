package com.openu.a2017_app1;

import com.openu.a2017_app1.data.Dao;
import com.openu.a2017_app1.data.DaoFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.instanceOf;

@RunWith(MockitoJUnitRunner.class)
public class DaoFactoryTest {

    @Mock
    Dao dao;

    @Before
    public void before() {
        DaoFactory.getInstance().setDefault(dao.getClass());
    }

    @Test
    public void factory_creates_correct_dao_object() throws Exception {
        assertNotNull(DaoFactory.getInstance().create());
        assertThat(DaoFactory.getInstance().create(), instanceOf(dao.getClass()));
    }

    @Test
    public void factory_create_different_dao_objects() throws Exception {
        Dao dao1 = DaoFactory.getInstance().create();
        Dao dao2 = DaoFactory.getInstance().create();
        assertThat(dao1, not(sameInstance(dao2)));
    }

    @Test
    public void factory_have_the_same_instance() throws Exception {
        DaoFactory factory1 = DaoFactory.getInstance();
        DaoFactory factory2 = DaoFactory.getInstance();
        assertThat(factory1, sameInstance(factory2));
    }
}