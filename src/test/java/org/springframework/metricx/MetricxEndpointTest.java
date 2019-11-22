package org.springframework.metricx;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.metricx.config.MetricxConfig;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class MetricxEndpointTest {

    private MetricxEndpoint metricxEndpoint;
    private SimpleMeterRegistry registry;
    private MetricxConfig metricxConfig;

    @Before
    public void setUp() throws Exception {
        registry = new SimpleMeterRegistry();
        metricxConfig = new MetricxConfig();
        metricxConfig.setExposurePrefix("counter");
        metricxEndpoint = new MetricxEndpoint(registry, metricxConfig);
    }

    @Test
    public void metricx_shouldReturnEmptyMap_When_NoMetricRegistered() {
        assertTrue(metricxEndpoint.metricx().isEmpty());
    }

    @Test
    public void metricx_shouldReturnAllMetricsRegistered() {
        registry.counter("counter.request.success").increment();
        registry.counter("counter.request.fail").increment();
        registry.counter("jvm.memory.used").increment();

        Map<String, Double> metricx = metricxEndpoint.metricx();
        assertSame(2, metricx.size());
        assertEquals(1, metricx.get("counter.request.success").intValue());
        assertEquals(1, metricx.get("counter.request.fail").intValue());
    }

    @Test
    public void metricPattern() {
        registry.counter("counter.request.success").increment();
        registry.counter("counter.response.success").increment();
        registry.counter("jvm.memory.used").increment();

        Map<String, Double> metricx = metricxEndpoint.metricPattern("counter.response.*");

        assertSame(1, metricx.size());
        assertEquals(1, metricx.get("counter.response.success").intValue());
    }
}