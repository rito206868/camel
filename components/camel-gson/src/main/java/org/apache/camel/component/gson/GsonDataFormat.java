/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import org.apache.camel.Exchange;
import org.apache.camel.spi.DataFormat;
import org.apache.camel.support.ServiceSupport;
import org.apache.camel.util.IOHelper;

/**
 * A <a href="http://camel.apache.org/data-format.html">data format</a> ({@link DataFormat})
 * using <a href="http://code.google.com/p/google-gson/">Gson</a> to marshal to and from JSON.
 */
public class GsonDataFormat extends ServiceSupport implements DataFormat {

    private Gson gson;
    private Class<?> unmarshalType;
    private Type unmarshalGenericType;
    private List<ExclusionStrategy> exclusionStrategies;
    private LongSerializationPolicy longSerializationPolicy;
    private FieldNamingPolicy fieldNamingPolicy;
    private FieldNamingStrategy fieldNamingStrategy;
    private boolean serializeNulls;
    private boolean prettyPrint;
    private String dateFormatPattern;

    public GsonDataFormat() {
        this(Map.class);
    }

    /**
     * Use the default Gson {@link Gson} and with a custom
     * unmarshal type
     *
     * @param unmarshalType the custom unmarshal type
     */
    public GsonDataFormat(Class<?> unmarshalType) {
        this(null, unmarshalType);
    }

    /**
     * Use the default Gson {@link Gson} and with a custom
     * unmarshal type and {@link ExclusionStrategy}
     *
     * @param unmarshalType the custom unmarshal type
     * @param exclusionStrategies one or more custom ExclusionStrategy implementations
     * @deprecated use the setter instead
     */
    @Deprecated
    public GsonDataFormat(Class<?> unmarshalType, ExclusionStrategy... exclusionStrategies) {
        this(null, unmarshalType);
        setExclusionStrategies(Arrays.asList(exclusionStrategies));
    }

    /**
     * Use a custom Gson mapper and and unmarshal type
     *
     * @param gson          the custom mapper
     * @param unmarshalType the custom unmarshal type
     */
    public GsonDataFormat(Gson gson, Class<?> unmarshalType) {
        this.gson = gson;
        this.unmarshalType = unmarshalType;
    }

    /**
     * Use the default Gson {@link Gson} and with a custom
     * unmarshal generic type
     *
     * @param unmarshalGenericType the custom unmarshal generic type
     */
    public GsonDataFormat(Type unmarshalGenericType) {
        this(null, unmarshalGenericType);
    }

    /**
     * Use a custom Gson mapper and and unmarshal token type
     *
     * @param gson          the custom mapper
     * @param unmarshalGenericType the custom unmarshal generic type
     */
    public GsonDataFormat(Gson gson, Type unmarshalGenericType) {
        this.gson = gson;
        this.unmarshalGenericType = unmarshalGenericType;
    }

    @Override
    public void marshal(Exchange exchange, Object graph, OutputStream stream) throws Exception {
        BufferedWriter writer = IOHelper.buffered(new OutputStreamWriter(stream, IOHelper.getCharsetName(exchange)));
        gson.toJson(graph, writer);
        writer.close();
    }

    @Override
    public Object unmarshal(Exchange exchange, InputStream stream) throws Exception {
        BufferedReader reader = IOHelper.buffered(new InputStreamReader(stream, IOHelper.getCharsetName(exchange)));
        Object result = null;
        
        if (this.unmarshalGenericType != null) {
            result = gson.fromJson(reader, this.unmarshalGenericType);
        } else {
            result = gson.fromJson(reader, this.unmarshalType);
        }

        reader.close();
        return result;
    }

    @Override
    protected void doStart() throws Exception {
        if (gson == null) {
            GsonBuilder builder = new GsonBuilder();
            if (exclusionStrategies != null && !exclusionStrategies.isEmpty()) {
                ExclusionStrategy[] strategies = exclusionStrategies.toArray(new ExclusionStrategy[exclusionStrategies.size()]);
                builder.setExclusionStrategies(strategies);
            }
            if (longSerializationPolicy != null) {
                builder.setLongSerializationPolicy(longSerializationPolicy);
            }
            if (fieldNamingPolicy != null) {
                builder.setFieldNamingPolicy(fieldNamingPolicy);
            }
            if (fieldNamingStrategy != null) {
                builder.setFieldNamingStrategy(fieldNamingStrategy);
            }
            if (serializeNulls) {
                builder.serializeNulls();
            }
            if (prettyPrint) {
                builder.setPrettyPrinting();
            }
            if (dateFormatPattern != null) {
                builder.setDateFormat(dateFormatPattern);
            }
            gson = builder.create();
        }
    }

    @Override
    protected void doStop() throws Exception {
        // noop
    }

    // Properties
    // -------------------------------------------------------------------------

    public Class<?> getUnmarshalType() {
        return this.unmarshalType;
    }

    public void setUnmarshalType(Class<?> unmarshalType) {
        this.unmarshalType = unmarshalType;
    }

    public Type getUnmarshalGenericType() {
        return this.unmarshalType;
    }

    public void setUnmarshalGenericType(Type unmarshalGenericType) {
        this.unmarshalGenericType = unmarshalGenericType;
    }

    public List<ExclusionStrategy> getExclusionStrategies() {
        return exclusionStrategies;
    }

    public void setExclusionStrategies(List<ExclusionStrategy> exclusionStrategies) {
        this.exclusionStrategies = exclusionStrategies;
    }

    public LongSerializationPolicy getLongSerializationPolicy() {
        return longSerializationPolicy;
    }

    public void setLongSerializationPolicy(LongSerializationPolicy longSerializationPolicy) {
        this.longSerializationPolicy = longSerializationPolicy;
    }

    public FieldNamingPolicy getFieldNamingPolicy() {
        return fieldNamingPolicy;
    }

    public void setFieldNamingPolicy(FieldNamingPolicy fieldNamingPolicy) {
        this.fieldNamingPolicy = fieldNamingPolicy;
    }

    public FieldNamingStrategy getFieldNamingStrategy() {
        return fieldNamingStrategy;
    }

    public void setFieldNamingStrategy(FieldNamingStrategy fieldNamingStrategy) {
        this.fieldNamingStrategy = fieldNamingStrategy;
    }

    /**
     * @deprecated use {@link #isSerializeNulls()} instead
     */
    @Deprecated
    public Boolean getSerializeNulls() {
        return serializeNulls;
    }

    public boolean isSerializeNulls() {
        return serializeNulls;
    }

    /**
     * @deprecated use {@link #setSerializeNulls(boolean)} instead
     */
    @Deprecated
    public void setSerializeNulls(Boolean serializeNulls) {
        this.serializeNulls = serializeNulls;
    }

    public void setSerializeNulls(boolean serializeNulls) {
        this.serializeNulls = serializeNulls;
    }

    /**
     * @deprecated use {@link #isPrettyPrint()} instead
     */
    @Deprecated
    public Boolean getPrettyPrinting() {
        return prettyPrint;
    }

    public boolean isPrettyPrint() {
        return prettyPrint;
    }

    /**
     * @deprecated use {@link #setPrettyPrint(boolean)} instead
     */
    @Deprecated
    public void setPrettyPrinting(Boolean prettyPrinting) {
        this.prettyPrint = prettyPrinting;
    }

    public void setPrettyPrint(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
    }

    public String getDateFormatPattern() {
        return dateFormatPattern;
    }

    public void setDateFormatPattern(String dateFormatPattern) {
        this.dateFormatPattern = dateFormatPattern;
    }

    public Gson getGson() {
        return this.gson;
    }

}
