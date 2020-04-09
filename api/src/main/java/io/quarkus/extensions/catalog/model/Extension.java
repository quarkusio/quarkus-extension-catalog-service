package io.quarkus.extensions.catalog.model;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value.Immutable;

/**
 * An extension is a Maven dependency that can be added to a Quarkus project
 */
@Immutable
@JsonDeserialize(builder = ExtensionBuilder.class)
public interface Extension {

    String getGroupId();

    String getArtifactId();

    List<Release> getReleases();
}