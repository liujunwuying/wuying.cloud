package com.wuying.cloud.transaction.async.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang.StringUtils;

/**
 * json工具类
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-13
 */
public class JsonUtil {
    private static final ObjectMapper mapper = initObjectMapper();

    private static ObjectMapper initObjectMapper() {
        return new ObjectMapper().configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
                .configure(Feature.ALLOW_SINGLE_QUOTES, true)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static String writeValueAsString(Object value) {
        try {
            return value == null ? null : mapper.writeValueAsString(value);
        } catch (JsonProcessingException jpe) {
            throw new IllegalArgumentException(jpe);
        }
    }

    public static <T> T readValue(String content, Class<T> type) {
        try {
            return StringUtils.isEmpty(content) ? null : mapper.readValue(content, type);
        } catch (JsonProcessingException jpe) {
            throw new IllegalArgumentException(jpe);
        }
    }

    public static <T> T[] readArrayValue(String content, Class<T> elementClass) {
        try {
            return StringUtils.isEmpty(content) ? null : mapper.readValue(content, mapper.getTypeFactory().constructArrayType(elementClass));
        } catch (JsonProcessingException jpe) {
            throw new IllegalArgumentException(jpe);
        }
    }

    public static <T> T convertValue(Object object, Class<T> clazz) {
        return object == null ? null : mapper.convertValue(object, clazz);
    }
}
