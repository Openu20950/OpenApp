package com.openu.a2017_app1;

import com.openu.a2017_app1.data.parse.Converters;
import com.openu.a2017_app1.models.LocationPoint;
import com.openu.a2017_app1.models.Model;
import com.parse.ParseGeoPoint;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.Random;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class LocationConverterTest {

    Converters converters;
    @Mock
    LocationPoint point;

    @Before
    public void before() {
        converters = Converters.getInstance();
    }

    @Test
    public void convert_location_point_to_parse_geo_point() throws Exception {
        when(point.getLatitude()).thenReturn(randomLatitude());
        when(point.getLongitude()).thenReturn(randomLongtitude());

        ParseGeoPoint converted = (ParseGeoPoint) converters.convert(point);
        assertThat(converted.getLatitude(), equalTo(point.getLatitude()));
        assertThat(converted.getLongitude(), equalTo(point.getLongitude()));
    }

    @Test
    public void convert_location_point_from_parse_geo_point() throws Exception {
        ParseGeoPoint converted = new ParseGeoPoint(randomLatitude(), randomLongtitude());

        LocationPoint point = (LocationPoint) converters.convertBack(converted);
        assertThat(converted.getLatitude(), equalTo(point.getLatitude()));
        assertThat(converted.getLongitude(), equalTo(point.getLongitude()));
    }

    private double randomLatitude() {
        return Math.random() * 180 - 90; // -90.0 to 90.0
    }

    private double randomLongtitude() {
        return Math.random() * 360 - 180; // -180.0 to 180.0
    }
}