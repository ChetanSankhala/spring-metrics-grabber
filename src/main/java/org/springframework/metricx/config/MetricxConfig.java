package org.springframework.metricx.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Configuration
@Getter
@Setter
public class MetricxConfig {

    /**
     * IF exposurePrefix is configured 'counter' THEN
     *      '/metricx' endpoint will publish metrics starting with 'counter only'
     * Default: empty, means publish all
     */
    @NonNull
    @Value("${metricx.exposure.prefix:}")
    private String exposurePrefix;
}