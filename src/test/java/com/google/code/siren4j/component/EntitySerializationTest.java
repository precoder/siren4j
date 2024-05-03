/*******************************************************************************************
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2013 Erik R Serating
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
 * persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *********************************************************************************************/
package com.google.code.siren4j.component;

import com.google.code.siren4j.Siren4J;
import com.google.code.siren4j.component.builder.ActionBuilder;
import com.google.code.siren4j.component.builder.EntityBuilder;
import com.google.code.siren4j.component.builder.FieldBuilder;
import com.google.code.siren4j.component.builder.LinkBuilder;
import com.google.code.siren4j.component.impl.ActionImpl.Method;
import com.google.code.siren4j.component.testpojos.MethodNotBackedByProperty;
import com.google.code.siren4j.component.testpojos.MethodNotBackedByPropertyWithOverride;
import com.google.code.siren4j.component.testpojos.Review;
import com.google.code.siren4j.component.testpojos.Video;
import com.google.code.siren4j.component.testpojos.Video.Rating;
import com.google.code.siren4j.converter.ReflectingConverter;
import com.google.code.siren4j.converter.ResourceConverter;
import com.google.code.siren4j.meta.FieldType;
import com.google.code.siren4j.resource.CollectionResource;
import com.google.code.siren4j.util.TimezoneSaver;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.google.code.siren4j.util.TestUtil.loadResource;
import static org.junit.Assert.assertThat;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

public class EntitySerializationTest {

    private Date testDate;

    @ClassRule
    public static final TimezoneSaver TIMEZONE_SAVER = new TimezoneSaver("GMT+3:00");

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        testDate = new SimpleDateFormat(Siren4J.ISO8601_DATE_FORMAT).parse("2016-10-11T19:53:32+03:00");
    }

    @Test
    @Ignore
    public void testEntityToJsonSerialization() throws Exception {
        Entity orders = createTestEntity();
        System.out.println(orders.toString());
    }

    @Test
    public void testCanSerializeFieldNotBackedByProperty() throws Exception {
        MethodNotBackedByProperty summary = new MethodNotBackedByProperty();

        ResourceConverter converter = ReflectingConverter.newInstance();
        Entity entity = converter.toEntity(summary);

        assertThat(entity.toString(),
                sameJSONAs(loadResource("/serialization/method-not-backed-by-property.json")));
    }

    @Test
    public void testCanSerializeFieldNotBackedByPropertyWithOverride() throws Exception {
        MethodNotBackedByPropertyWithOverride summary = new MethodNotBackedByPropertyWithOverride();

        ResourceConverter converter = ReflectingConverter.newInstance();
        Entity entity = converter.toEntity(summary);

        assertThat(entity.toString(),
                sameJSONAs(loadResource("/serialization/method-not-backed-by-property-with-override.json")));
    }

    @Test
    public void testVideoSerialize() throws Exception {

        CollectionResource<Review> reviews = new CollectionResource<Review>();
        Review rev1 = givenReview("1", "Fred", "I loved it!!", testDate);;
        reviews.add(rev1);

        Review rev2 = givenReview("1", "John", "Overwelmed!!!!", testDate);
        reviews.add(rev2);

        Video video = givenVideo(reviews);

        ResourceConverter converter = ReflectingConverter.newInstance();
        Entity videoEntity = converter.toEntity(video);
        assertThat(videoEntity.toString(), sameJSONAs(loadResource("/serialization/video.json")));

        Object obj = converter.toObject(videoEntity);
        Entity entityRestored = converter.toEntity(obj);
        assertThat(entityRestored.toString(), sameJSONAs(loadResource("/serialization/video-restored.json")));

        reviews.setOverrideUri("/override");
        Entity revEnt = converter.toEntity(reviews);

        assertThat(revEnt.toString(), sameJSONAs(loadResource("/serialization/reviews.json")));
    }

    private Video givenVideo(CollectionResource<Review> reviews) {
        Video video = new Video();
        video.setId("z1977");
        video.setName("Star Wars");
        video.setDescription("An epic science fiction space opera");
        video.setRating(Rating.PG);
        video.setGenre("scifi");
        video.setReviews(reviews);
        return video;
    }

    private Review givenReview(String id, String reviewer, String body, Date testDate) {
        Review rev2 = new Review();
        rev2.setId(id);
        rev2.setReviewer(reviewer);
        rev2.setBody(body);
        rev2.setReviewdate(testDate);
        return rev2;
    }

    private Entity createTestEntity() {

        //List item 1
        List<Entity> embedded1 = new ArrayList<Entity>();
        Map<String, Object> props1 = new HashMap<String, Object>();
        List<Link> links1 = new ArrayList<Link>();
        embedded1.add(createEmbeddedEntity("/baskets/98712", "basket"));
        embedded1.add(createEmbeddedEntity("/customer/7809", "basket"));
        props1.put("total", 30.00f);
        props1.put("currency", "USD");
        props1.put("status", "shipped");
        links1.add(createLink("/orders/123", Link.RELATIONSHIP_SELF));

        Entity listItem1 = createEntity(
                new String[]{"order", "list-item"},
                new String[]{"order"},
                props1,
                embedded1,
                links1,
                null
        );

        //List item 2
        List<Entity> embedded2 = new ArrayList<Entity>();
        Map<String, Object> props2 = new HashMap<String, Object>();
        List<Link> links2 = new ArrayList<Link>();
        embedded2.add(createEmbeddedEntity("/baskets/98713", "basket"));
        embedded2.add(createEmbeddedEntity("/customer/12369", "basket"));
        props2.put("total", 20.00f);
        props2.put("currency", "USD");
        props2.put("status", "processing");
        links2.add(createLink("/orders/124", Link.RELATIONSHIP_SELF));

        Entity listItem2 = createEntity(
                new String[]{"order", "list-item"},
                new String[]{"order"},
                props2,
                embedded2,
                links2,
                null
        );

        List<Action> orderActions = new ArrayList<Action>();
        List<Entity> orderEntities = new ArrayList<Entity>();
        List<Link> orderLinks = new ArrayList<Link>();
        Map<String, Object> orderProps = new HashMap<String, Object>();
        List<Field> actionFields = new ArrayList<Field>();

        actionFields.add(createField("id", FieldType.NUMBER, null));

        orderActions.add(createAction(
                "find-order",
                new String[]{"find-order"},
                (String) null,
                (Method) null,
                "/orders",
                (String) null,
                actionFields
        ));

        orderEntities.add(listItem1);
        orderEntities.add(listItem2);

        orderLinks.add(createLink("/orders", Link.RELATIONSHIP_SELF));
        orderLinks.add(createLink("/orders?page=2", Link.RELATIONSHIP_NEXT));

        orderProps.put("currentlyProcessing", 14);
        orderProps.put("shippedToday", 20);

        Entity orders = createEntity(new String[]{"orders"}, null, orderProps, orderEntities, orderLinks, orderActions);
        return orders;
    }

    private Entity createEmbeddedEntity(String href, String... rel) {
        EntityBuilder builder = EntityBuilder.newInstance();
        return builder.setRelationship(rel).setHref(href).build();
    }

    private Entity createEntity(String[] entityClass, String[] rel, Map<String, Object> props,
                                List<Entity> entities, List<Link> links, List<Action> actions) {
        EntityBuilder builder = EntityBuilder.newInstance();
        if (!ArrayUtils.isEmpty(entityClass)) {
            builder.setComponentClass(entityClass);
        }
        if (!ArrayUtils.isEmpty(rel)) {
            builder.setRelationship(rel);
        }
        if (!MapUtils.isEmpty(props)) {
            builder.addProperties(props);
        }
        if (!CollectionUtils.isEmpty(entities)) {
            builder.addSubEntities(entities);
        }
        if (!CollectionUtils.isEmpty(links)) {
            builder.addLinks(links);
        }
        if (!CollectionUtils.isEmpty(actions)) {
            builder.addActions(actions);
        }

        return builder.build();
    }

    private Link createLink(String href, String... rel) {
        LinkBuilder builder = LinkBuilder.newInstance();
        return builder.setRelationship(rel).setHref(href).build();
    }

    private Field createField(String name, FieldType type, String value) {
        FieldBuilder builder = FieldBuilder.newInstance();
        builder.setName(name)
                .setType(type);
        if (StringUtils.isNotBlank(value)) {
            builder.setValue(value);
        }
        return builder.build();

    }

    private Action createAction(String name, String[] actionClass, String title, Method method, String href, String type, List<Field> fields) {
        ActionBuilder builder = ActionBuilder.newInstance();

        builder.setName(name)
                .setHref(href);
        if (StringUtils.isNotBlank(title)) {
            builder.setTitle(title);
        }
        if (method != null) {
            builder.setMethod(method);
        }
        if (StringUtils.isNotBlank(type)) {
            builder.setType(type);
        }
        if (!CollectionUtils.isEmpty(fields)) {
            builder.addFields(fields);
        }
        if (ArrayUtils.isEmpty(actionClass)) {
            builder.setComponentClass(actionClass);
        }
        return builder.build();


    }


}
