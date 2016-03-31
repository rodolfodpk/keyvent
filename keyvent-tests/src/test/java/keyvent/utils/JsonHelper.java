package keyvent.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonHelper<T> {

    private final ObjectMapper mapper;

    public JsonHelper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public T fromJson(String json, Class<T> clazz) throws IOException {
        return mapper.readValue(json, clazz);
    }

    public T fromJsonAt(String at, String json, Class<?> clazz) throws Exception {
        return mapper.readerFor(clazz).at(at).readValue(json);
    }

}
