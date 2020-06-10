package io.quarkus.registry.model;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = ReleaseBuilder.class)
public interface Release extends Comparable<Release> {

    String getVersion();

    @Nullable
    @JsonProperty("quarkus-core")
    String getQuarkusCore();

    @JsonProperty("repository-url")
    @Value.Auxiliary
    @Nullable
    String getRepositoryURL();

    static ReleaseBuilder builder() {
        return new ReleaseBuilder();
    }

    @Override
    default int compareTo(Release o) {
        //TODO: Compare using SemVer rules
        int compare = getVersion().compareTo(o.getVersion());
        if (compare == 0) {
            compare = getQuarkusCore().compareTo(o.getQuarkusCore());
        }
        return compare;
    }
}
