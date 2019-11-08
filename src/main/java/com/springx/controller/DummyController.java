package com.springx.controller;

import javax.annotation.PostConstruct;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;

@RestController
public class DummyController {

    private MeterRegistry registry;

    @Autowired
    public DummyController(MeterRegistry registry) {
        this.registry = registry;
    }

    @PostConstruct
    public void setUp(){
        registry.counter("counter.test.request").increment();
    }

    @RequestMapping(path = "/hit", method = {RequestMethod.GET})
    public void test() {
        registry.counter("counter.test.request").increment();
    }

    @RequestMapping(path = "/metrics", method = {RequestMethod.GET})
    public Set<String> metric() {
        Set<String> names = new LinkedHashSet<>();
        collectNames(names, this.registry);
        return names;
    }

    private void collectNames(Set<String> names, MeterRegistry registry) {
        if (registry instanceof CompositeMeterRegistry) {
            ((CompositeMeterRegistry) registry).getRegistries()
                    .forEach((member) -> collectNames(names, member));
        } else {
            registry.getMeters().stream().map(this::getMetricEntry).forEach(names::add);
        }
    }

    private String getMetricEntry(Meter meter) {
        double value = meter.measure().iterator().next().getValue();
        return meter.getId().getName() + ":" + value;
    }
}