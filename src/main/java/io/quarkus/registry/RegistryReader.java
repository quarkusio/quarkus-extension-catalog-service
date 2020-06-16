package io.quarkus.registry;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import io.quarkus.registry.model.Registry;
import io.quarkus.registry.model.RegistryBuilder;

public class RegistryReader {

    private final List<URL> urls = new ArrayList<>();

    public RegistryReader addURL(URL url) {
        urls.add(url);
        return this;
    }

    public Registry create() throws IOException {
        if (urls.isEmpty()) {
            throw new IllegalStateException("At least one URL must be specified");
        }
        ObjectMapper mapper = new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);
        if (urls.size() == 1) {
            // Just one
            return mapper.readValue(urls.get(0), Registry.class);
        } else {
            RegistryBuilder builder = Registry.builder();
            for (URL url : urls) {
                Registry aRegistry = mapper.readValue(url, Registry.class);
                builder.addAllCategories(aRegistry.getCategories())
                        .addAllExtensions(aRegistry.getExtensions())
                        .addAllPlatforms(aRegistry.getPlatforms())
                        .addAllVersions(aRegistry.getVersions());
            }
            return builder.build();
        }

    }
}
