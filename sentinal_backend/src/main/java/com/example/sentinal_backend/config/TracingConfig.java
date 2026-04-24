package com.example.sentinal_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.otel.bridge.OtelTracer;
import io.micrometer.tracing.otel.bridge.OtelCurrentTraceContext;
import io.micrometer.tracing.handler.DefaultTracingObservationHandler;
import io.micrometer.tracing.handler.PropagatingSenderTracingObservationHandler;
import io.micrometer.tracing.handler.PropagatingReceiverTracingObservationHandler;
import io.micrometer.tracing.propagation.Propagator;
import io.micrometer.tracing.otel.bridge.OtelPropagator;
import org.springframework.kafka.core.KafkaTemplate;
import io.micrometer.observation.ObservationRegistry;

@Configuration
public class TracingConfig {

    @Value("${management.otlp.tracing.endpoint:http://127.0.0.1:4318/v1/traces}")
    private String otlpEndpoint = "http://127.0.0.1:4318/v1/traces";

    @Bean
    public OtlpHttpSpanExporter otlpHttpSpanExporter() {
        return OtlpHttpSpanExporter.builder()
                .setEndpoint(otlpEndpoint)
                .build();
    }

    @Bean
    public OpenTelemetry openTelemetry(OtlpHttpSpanExporter exporter) {
        Resource resource = Resource.getDefault().merge(
            Resource.create(Attributes.of(AttributeKey.stringKey("service.name"), "sentinal-backend"))
        );

        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(BatchSpanProcessor.builder(exporter).build())
                .setResource(resource)
                .build();

        return OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
                .build();
    }

    @Bean
    public Tracer tracer(OpenTelemetry openTelemetry) {
        OtelCurrentTraceContext otelCurrentTraceContext = new OtelCurrentTraceContext();
        return new OtelTracer(
            openTelemetry.getTracer("sentinal-backend"),
            otelCurrentTraceContext,
            event -> { }
        );
    }

    @Bean
    public Propagator propagator(OpenTelemetry openTelemetry) {
        return new OtelPropagator(openTelemetry.getPropagators(), openTelemetry.getTracer("sentinal-backend"));
    }

    @Configuration
    public static class ObservationRegistryConfig {
        @Autowired
        public void configureObservationRegistry(ObservationRegistry registry, Tracer tracer, Propagator propagator) {
            registry.observationConfig().observationHandler(new PropagatingSenderTracingObservationHandler<>(tracer, propagator));
            registry.observationConfig().observationHandler(new PropagatingReceiverTracingObservationHandler<>(tracer, propagator));
            registry.observationConfig().observationHandler(new DefaultTracingObservationHandler(tracer));
        }

        @Autowired
        public void configureKafka(KafkaTemplate<?, ?> kafkaTemplate, ObservationRegistry registry) {
            kafkaTemplate.setObservationEnabled(true);
            kafkaTemplate.setObservationRegistry(registry);
        }
    }
}
