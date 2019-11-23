package org.springframework.metricx;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.springframework.http.MediaType;
import org.springframework.metricx.config.MetricxConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;

@RestController
public class MetricxEndpoint {

    private MeterRegistry registry;
    private MetricxConfig metricxConfig;

    public MetricxEndpoint(MeterRegistry registry,
                           MetricxConfig metricxConfig) {
        this.registry = registry;
        this.metricxConfig = metricxConfig;
    }

    @GetMapping(value = "/metricx", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Double> metricx() {
        Map<String, Double> matricKeyValue = new LinkedHashMap<>();
        collectMetrics(matricKeyValue, this.registry,
                metricName -> metricName.startsWith(metricxConfig.getExposurePrefix()));
        return matricKeyValue;
    }

    private void collectMetrics(Map<String, Double> matricKeyValue, MeterRegistry registry, Predicate<String> filter) {
        if (registry instanceof CompositeMeterRegistry) {
            ((CompositeMeterRegistry) registry).getRegistries()
                    .forEach(member -> collectMetrics(matricKeyValue, member, filter));
        } else {
            registry.getMeters()
                    .stream()
                    .filter(meter -> filter.test(meter.getId().getName()))
                    .forEach(meter ->
                        matricKeyValue.put(meter.getId().getName(), meter.measure().iterator().next().getValue())
                    );
        }
    }

    @GetMapping(value = "/metricx/{metricPattern}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Double> metricPattern(@PathVariable("metricPattern") String metricPattern) {
        Map<String, Double> matricsKeyValue = new LinkedHashMap<>();
        collectMetrics(matricsKeyValue, this.registry,
                metricName ->
                        metricName.startsWith(metricxConfig.getExposurePrefix())
                            && Pattern.matches(metricPattern, metricName));
        return matricsKeyValue;
    }
}
