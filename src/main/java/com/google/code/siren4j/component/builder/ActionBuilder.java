/*******************************************************************************************
 * The MIT License (MIT)
 * 
 * Copyright (c) 2013 Erik R Serating
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *********************************************************************************************/
package com.google.code.siren4j.component.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import com.google.code.siren4j.component.Action;
import com.google.code.siren4j.component.Field;
import com.google.code.siren4j.component.impl.ActionImpl;
import com.google.code.siren4j.component.impl.ActionImpl.Method;
import com.google.code.siren4j.component.impl.FieldImpl;
import com.google.code.siren4j.error.Siren4JBuilderValidationException;
import org.apache.commons.lang3.StringUtils;

public class ActionBuilder extends BaseBuilder<Action> {

    private List<Field> fields = new ArrayList<Field>();
    private List<Field> urlParams = new ArrayList<Field>();
    private List<Field> headers = new ArrayList<Field>();

    private ActionBuilder() {

    }
    /**
     * Retrieve a new instance of an <code>ActionBuilder</code>.
     * @return new instance, never <code>null</code>.
     */
    public static ActionBuilder newInstance() {
        return new ActionBuilder();
    }

    /**
     * Retrieve a new instance of an <code>ActionBuilder</code>. Added to allow this method to be used with
     * static method importing.
     * @since 1.1.0
     * @return new instance, never <code>null</code>.
     */
    public static ActionBuilder createActionBuilder() {
        return newInstance();
    }

    /**
     * Set the component class of the entity to be built. This method can be called many times
     * but only the value of the last call is used in the built entity. This is an optional property as specified
     * by the Siren specification.
     * @param componentClass may be <code>null</code> or empty.
     * @return <code>this</code> builder, never <code>null</code>.
     */
    public ActionBuilder setComponentClass(String... componentClass) {
        addStep("setComponentClass", new Object[] { componentClass }, new Class<?>[] {String[].class});
        return this;
    }

    /**
     * Sets an actions name value.
     * @param name cannot be <code>null</code> or empty.
     * @return
     */
    public ActionBuilder setName(String name) {
        if(StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("name cannot be null or empty.");
        }
        addStep("setName", new Object[] { name });
        return this;
    }

    public ActionBuilder setMethod(Method method) {
        addStep("setMethod", new Object[] { method }, new Class<?>[] {Method.class});
        return this;
    }

    public ActionBuilder setHref(String href) {
        if(StringUtils.isBlank(href)) {
            throw new IllegalArgumentException("href cannot be null or empty.");
        }
        addStep("setHref", new Object[] { href });
        return this;
    }

    public ActionBuilder setTitle(String title) {
        addStep("setTitle", new Object[] { title }, new Class<?>[] {String.class});
        return this;
    }

    public ActionBuilder setType(String type) {
        addStep("setType", new Object[] { type }, new Class<?>[] {String.class});
        return this;
    }

    public ActionBuilder addField(Field field) {
        addStep("_addField", new Object[] { field }, true);
        return this;
    }

    public ActionBuilder addFields(List<Field> fields) {
        for (Field f : fields) {
            addField(f);
        }
        return this;
    }

    protected void _addField(FieldImpl field) {
        fields.add(field);
    }

    public ActionBuilder addUrlParam(Field param) {
        addStep("_addUrlParam", new Object[] { param }, true);
        return this;
    }

    public ActionBuilder addUrlParams(List<Field> params) {
        for (Field f : params) {
            addUrlParam(f);
        }
        return this;
    }

    protected void _addUrlParam(FieldImpl param) {
        urlParams.add(param);
    }

    public ActionBuilder addHeader(Field header) {
        addStep("_addHeader", new Object[] { header }, true);
        return this;
    }

    public ActionBuilder addHeaders(List<Field> headers) {
        for (Field f : headers) {
            addHeader(f);
        }
        return this;
    }

    protected void _addHeader(FieldImpl header) {
        headers.add(header);
    }

    public ActionBuilder setMetaData(Map<String, String> metaData) {
        addStep("setMetaData", new Object[] {metaData}, new Class[] {Map.class});
        return this;
    }

    @Override
    protected void postProcess(Action obj) {
        ActionImpl action = (ActionImpl) obj;
        if (!CollectionUtils.isEmpty(fields)) {
            action.setFields(fields);
        }
        if (!CollectionUtils.isEmpty(urlParams)) {
            action.setUrlParams(urlParams);
        }
        if (!CollectionUtils.isEmpty(headers)) {
            action.setHeaders(headers);
        }
    }

    @Override
    protected void validate(Action obj) {
        String requiredMsg = "Required property.";
        if (StringUtils.isBlank(obj.getName())) {
            throw new Siren4JBuilderValidationException("name", obj.getClass(), requiredMsg);
        }
        if (obj.getHref() == null) {
            throw new Siren4JBuilderValidationException("href", obj.getClass(), requiredMsg);
        }
    }

    @Override
    protected Action createInstance() {
        return new ActionImpl();
    }

}
