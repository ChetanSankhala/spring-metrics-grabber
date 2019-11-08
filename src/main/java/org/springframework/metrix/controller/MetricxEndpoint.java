package org.springframework.metrix.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;

@RestController("/metricx")
public class MetricxEndpoint {

    private MeterRegistry registry;

    public MetricxEndpoint(MeterRegistry registry) {
        this.registry = registry;
    }

    @GetMapping
    public Map<String, Double> metric() {
        Map<String, Double> names = new LinkedHashMap<>();
        collectNames(names, this.registry);
        return names;
    }

    private void collectNames(Map<String, Double> names, MeterRegistry registry) {
        if (registry instanceof CompositeMeterRegistry) {
            ((CompositeMeterRegistry) registry).getRegistries()
                    .forEach((member) -> collectNames(names, member));
        } else {
            registry.getMeters()
                    .forEach(meter ->
                        names.put(meter.getId().getName(), meter.measure().iterator().next().getValue())
                    );
        }
    }
}
