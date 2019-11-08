package org.springframework.metricx.controller;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;

@RestController
public class MetricxEndpoint {

    private MeterRegistry registry;

    public MetricxEndpoint(MeterRegistry registry) {
        this.registry = registry;
    }

    @GetMapping("/metricx")
    public Map<String, Double> metricx() {
        Map<String, Double> matricKeyValue = new LinkedHashMap<>();
        collectMetrics(matricKeyValue, this.registry, (String) -> true);
        return matricKeyValue;
    }

    private void collectMetrics(Map<String, Double> matricKeyValue, MeterRegistry registry, Predicate<String> filter) {
        if (registry instanceof CompositeMeterRegistry) {
            ((CompositeMeterRegistry) registry).getRegistries()
                    .forEach((member) -> collectMetrics(matricKeyValue, member, filter));
        } else {
            registry.getMeters()
                    .stream()
                    .filter(meter -> filter.test(meter.getId().getName()))
                    .forEach(meter ->
                        matricKeyValue.put(meter.getId().getName(), meter.measure().iterator().next().getValue())
                    );
        }
    }

    @GetMapping("/metricx/{metricPattern}")
    public Map<String, Double> metricPattern(@PathVariable("metricPattern") String metricPattern) {
        Map<String, Double> matricsKeyValue = new LinkedHashMap<>();
        collectMetrics(matricsKeyValue, this.registry, metricName -> Pattern.matches(metricPattern, metricName));
        return matricsKeyValue;
    }
}
