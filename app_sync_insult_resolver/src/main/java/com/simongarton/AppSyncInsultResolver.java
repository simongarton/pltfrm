package com.simongarton;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class AppSyncInsultResolver implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private final List<String> insults;

    public AppSyncInsultResolver() {

        this.insults = this.loadInsults();
    }

    public String getInsult() {

        final Random random = new Random();
        final int insultIndex = random.nextInt(this.insults.size());
        return this.insults.get(insultIndex);
    }

    private List<String> loadInsults() {

        final String resourcePath = "insults.txt";
        final InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            return List.of("You are a silly person, aren't you, but I can't find my insult list.");
        }

        final List<String> insults = new ArrayList<>();
        try (
                final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                insults.add(line);
            }

            return insults;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, Object> handleRequest(final Map<String, Object> stringObjectMap, final Context context) {

        final Map<String, Object> arguments = (Map<String, Object>) stringObjectMap.get("arguments");

        int insultIndex;
        if (arguments == null || !arguments.containsKey("id")) {
            final Random random = new Random();
            insultIndex = random.nextInt(this.insults.size());
        } else {
            insultIndex = (Integer) arguments.get("id");
        }
        if (insultIndex > this.insults.size()) {
            insultIndex = 0;
        }

        final Map<String, Object> response = new HashMap<>();
        response.put("insultId", insultIndex);
        response.put("insult", this.insults.get(insultIndex));

        return response;
    }
}

