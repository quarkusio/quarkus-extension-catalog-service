package io.quarkus.extensions.catalog;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import io.quarkus.dependencies.Category;
import io.quarkus.dependencies.Extension;
import io.quarkus.extensions.catalog.model.Platform;
import io.quarkus.extensions.catalog.model.PlatformBuilder;
import io.quarkus.extensions.catalog.model.Release;
import io.quarkus.extensions.catalog.model.ReleaseBuilder;
import io.quarkus.extensions.catalog.model.Repository;
import io.quarkus.extensions.catalog.spi.IndexVisitor;
import io.quarkus.platform.descriptor.QuarkusPlatformDescriptor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class IndexerTest {
    @Test
    void shouldFormatURL() {
        Release release = new ReleaseBuilder().version("1.3.1.Final").build();
        Platform platform = new PlatformBuilder()
                .groupId("io.quarkus")
                .artifactId("quarkus-universe-bom")
                .addReleases(release).build();
        URL url = Indexer.getPlatformJSONURL(platform, release);
        assertThat(url).hasPath("/maven2/io/quarkus/quarkus-universe-bom/1.3.1.Final/quarkus-universe-bom-1.3.1.Final.json");
    }


    @Test
    void shouldParseIntoDescriptor() throws IOException {
        Release release = new ReleaseBuilder().version("1.3.1.Final").build();
        Platform platform = new PlatformBuilder()
                .groupId("io.quarkus")
                .artifactId("quarkus-universe-bom")
                .addReleases(release).build();
        Indexer indexer = new Indexer();
        QuarkusPlatformDescriptor descriptor = indexer.readPlatformDescriptor(platform, release);
        descriptor.getCategories().stream().map(Category::getName).forEach(System.out::println);
        assertThat(descriptor).isNotNull();
    }

    @Test
    void shouldVisitParsedElements() throws Exception {
        Path rootPath = Paths.get("../playground/repository");
        assertThat(rootPath).exists();
        ObjectMapper objectMapper = new ObjectMapper()
                .enable(JsonParser.Feature.ALLOW_COMMENTS)
                .enable(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS)
                .setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);

        Repository repository = Repository.parse(rootPath, objectMapper);
        Indexer indexer = new Indexer();
        IndexVisitor mock = mock(IndexVisitor.class);
        indexer.index(repository, mock);
        verify(mock, times(4)).visitPlatform(any(QuarkusPlatformDescriptor.class));
        verify(mock, times(2)).visitExtension(any(Extension.class), anyString());
    }
}