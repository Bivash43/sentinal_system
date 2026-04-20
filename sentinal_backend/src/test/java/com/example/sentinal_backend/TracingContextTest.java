package com.example.sentinal_backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import io.micrometer.tracing.Tracer;
import io.micrometer.observation.ObservationRegistry;

@SpringBootTest
public class TracingContextTest {
    @Autowired
    private ApplicationContext context;

    @Test
    public void testTracing() {
        System.out.println("=================================================");
        if (context.containsBean("tracer")) {
            System.out.println("TRACER BEAN EXISTS: " + context.getBean(Tracer.class).getClass().getName());
        } else {
            System.out.println("TRACER BEAN IS COMPLETELY MISSING!!!");
        }

        ObservationRegistry registry = context.getBean(ObservationRegistry.class);
        System.out.println("REGISTRY IS NO-OP: " + registry.isNoop());
        System.out.println("=================================================");
    }
}
