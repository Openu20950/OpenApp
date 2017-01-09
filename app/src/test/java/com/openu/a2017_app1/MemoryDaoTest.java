package com.openu.a2017_app1;

import com.openu.a2017_app1.data.Dao;
import com.openu.a2017_app1.data.DaoFactory;
import com.openu.a2017_app1.models.Model;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Random;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Created by Raz on 09/01/2017.
 */

public class MemoryDaoTest extends UnitTest {

    String tableName;

    Dao dao;

    @Before
    public void before() {
        dao = DaoFactory.getInstance().create();
        tableName = UUID.randomUUID().toString();
    }

    private Model createModel() {
        final Model model = mock(Model.class);
        when(model.getPrimaryKey()).thenReturn("id");
        when(model.getTable()).thenReturn(tableName);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return DaoFactory.getInstance().create().save(model);
            }
        }).when(model).save();

        when(model.getAttribute(model.getPrimaryKey())).thenReturn(null);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object id = invocation.getArgumentAt(1, Object.class);
                when(model.getAttribute(model.getPrimaryKey())).thenReturn(id);
                return null;
            }
        }).when(model).setAttribute(eq("id"), anyString());

        when(model.getAttribute("attr")).thenReturn(null);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object id = invocation.getArgumentAt(1, Object.class);
                when(model.getAttribute("attr")).thenReturn(id);
                return null;
            }
        }).when(model).setAttribute(eq("attr"), anyString());
        return model;
    }

    private Model[] createModels(int howMuch) {
        Model[] models = new Model[howMuch];
        for (int i = 0; i < howMuch; i++) {
            models[i] = createModel();
            models[i].setAttribute("attr", i);
            models[i].save();
        }
        return models;
    }


    @Test
    public void save_one_model() {
        Model model = createModel();
        model.save();

        assertThat(dao.query(model).getAll().size(), equalTo(1));
        assertThat(model.getAttribute(model.getPrimaryKey()), notNullValue());
    }

    @Test
    public void save_many_models() {
        Model model = null;
        for (int i = 0; i < 10; i++) {
            model = createModel();
            model.save();
        }

        assertThat(dao.query(model).getAll().size(), equalTo(10));
    }

    @Test
    public void find_model_return_same_instance() {
        Model model = createModel();
        model.save();

        assertThat(dao.query(model).find(model.getAttribute(model.getPrimaryKey())), sameInstance(model));
    }

    @Test
    public void find_model_with_wrong_id_return_null() {
        Model model = createModel();
        model.save();

        assertThat(dao.query(model).find(Math.random()), nullValue());
    }

    @Test
    public void filter_with_equal_operation() {
        Model model = createModels(10)[0];
        int index = new Random().nextInt(10);

        assertThat(dao.query(model).where("attr", "==", index).getAll().size(), equalTo(1));
    }

    @Test
    public void filter_with_not_equal_operation() {
        Model model = createModels(10)[0];
        int index = new Random().nextInt(10);

        assertThat(dao.query(model).where("attr", "!=", index).getAll().size(), equalTo(9));
    }

    @Test
    public void filter_with_grater_then_operation() {
        Model model = createModels(10)[0];
        int index = new Random().nextInt(10);

        assertThat(dao.query(model).where("attr", ">", index).getAll().size(), equalTo(9 - index));
    }

    @Test
    public void filter_with_grater_then_equals_operation() {
        Model model = createModels(10)[0];
        int index = new Random().nextInt(10);

        assertThat(dao.query(model).where("attr", ">=", index).getAll().size(), equalTo(10 - index));
    }

    @Test
    public void filter_with_less_then_operation() {
        Model model = createModels(10)[0];
        int index = new Random().nextInt(10);

        assertThat(dao.query(model).where("attr", "<", index).getAll().size(), equalTo(index));
    }

    @Test
    public void filter_with_less_then_equals_operation() {
        Model model = createModels(10)[0];
        int index = new Random().nextInt(10);

        assertThat(dao.query(model).where("attr", "<=", index).getAll().size(), equalTo(index + 1));
    }
}
