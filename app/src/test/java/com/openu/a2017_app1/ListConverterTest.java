package com.openu.a2017_app1;

import com.openu.a2017_app1.data.Converter;
import com.openu.a2017_app1.data.parse.Converters;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doAnswer;


@RunWith(MockitoJUnitRunner.class)
public class ListConverterTest {

    @Mock
    Converters converters;

    @Mock
    Converter stringConverter;

    List<String> list;
    List<String> listBack;

    @Before
    public void before() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object converted = invocation.callRealMethod();
                Object param = invocation.getArgumentAt(0, Object.class);
                if (converted.equals(param) && stringConverter.canConvert(param)) {
                    converted = stringConverter.convert(param);
                }
                return converted;
            }
        }).when(converters).convert(anyObject());
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object converted = invocation.callRealMethod();
                Object param = invocation.getArgumentAt(0, Object.class);
                if (converted.equals(param) && stringConverter.canConvertBack(param)) {
                    converted = stringConverter.convertBack(param);
                }
                return converted;
            }
        }).when(converters).convertBack(anyObject());
        Method method = Converters.class.getDeclaredMethod("initializeConverters");
        method.setAccessible(true);
        Object r = method.invoke(converters);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object value = invocation.getArgumentAt(0, Object.class);
                return value instanceof String;
            }
        }).when(stringConverter).canConvert(anyObject());
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object value = invocation.getArgumentAt(0, Object.class);
                return value instanceof String;
            }
        }).when(stringConverter).canConvertBack(anyObject());
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                String value = invocation.getArgumentAt(0, String.class);
                return "a" + value;
            }
        }).when(stringConverter).convert(anyObject());
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                String value = invocation.getArgumentAt(0, String.class);
                return value.substring(1, value.length());
            }
        }).when(stringConverter).convertBack(anyObject());

        list = new ArrayList<>();
        listBack = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            String random = UUID.randomUUID().toString();
            list.add(random);
            listBack.add("a" + random);
        }
    }

    @Test
    public void convert_list_of_strings() throws Exception {
        List<String> converted = (List<String>) converters.convert(list);

        assertThat(converted.size(), equalTo(list.size()));
        for (int i = 0; i < converted.size(); i++) {
            assertThat(converted.get(i), equalTo(listBack.get(i)));
        }
    }

    @Test
    public void convert_back_list_of_strings() throws Exception {
        List<String> convertedBack = (List<String>) converters.convertBack(listBack);

        assertThat(convertedBack.size(), equalTo(list.size()));
        for (int i = 0; i < convertedBack.size(); i++) {
            assertThat(convertedBack.get(i), equalTo(list.get(i)));
        }
    }
}