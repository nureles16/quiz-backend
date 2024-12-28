package com.example.quiz_app;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AuthorityDeserializer extends JsonDeserializer<List<SimpleGrantedAuthority>> {

    @Override
    public List<SimpleGrantedAuthority> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        List<Map<String, String>> authorities = jsonParser.readValueAs(List.class);

        return authorities.stream()
                .map(authorityMap -> new SimpleGrantedAuthority(authorityMap.get("authority")))
                .collect(Collectors.toList());
    }
}