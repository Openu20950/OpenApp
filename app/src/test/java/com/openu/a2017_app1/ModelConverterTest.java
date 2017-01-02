package com.openu.a2017_app1;

import com.openu.a2017_app1.data.parse.Converters;
import com.openu.a2017_app1.models.Model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class ModelConverterTest {

    Converters converters;
    @Mock
    Model model;

    @Before
    public void before() {
        converters = Converters.getInstance();

        String id = UUID.randomUUID().toString();
        when(model.getPrimaryKey()).thenReturn("id");
        when(model.getAttribute(model.getPrimaryKey())).thenReturn(null);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                String id = UUID.randomUUID().toString();
                when(model.getAttribute(model.getPrimaryKey())).thenReturn(id);
                return null;
            }
        }).when(model).save();
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                String id = invocation.getArgumentAt(1, String.class);
                when(model.getAttribute(model.getPrimaryKey())).thenReturn(id);
                return null;
            }
        }).when(model).setAttribute(eq("id"), anyString());
    }

    @Test
    public void convert_model_to_id() throws Exception {
        assertThat(converters.convert(model),
                both(
                        is(equalTo(model.getAttribute(model.getPrimaryKey()))))
                .and(
                        notNullValue()));
    }

}