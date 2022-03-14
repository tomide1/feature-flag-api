package com.iw.featureflag.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Feature {
    private String id;
    private String name;
    @Builder.Default
    private boolean enabled = false;

    public static final List<Feature> GLOBAL_FEATURES = Collections.singletonList(
            Feature.builder()
                    .id("e35004b5-2226-4efa-b3b0-2e0cdecf744f")
                    .name("Global Feature 1")
                    .enabled(true)
                    .build()
    );
}
