from app.services.kafka_consumer import start_kafka_worker
from app.core.config import settings

from opentelemetry import trace
from opentelemetry.sdk.trace import TracerProvider
from opentelemetry.sdk.trace.export import BatchSpanProcessor
from opentelemetry.exporter.otlp.proto.http.trace_exporter import OTLPSpanExporter
from opentelemetry.sdk.resources import Resource, SERVICE_NAME

if __name__ == "__main__":
    # 1. Setup OpenTelemetry
    resource = Resource(attributes={
        SERVICE_NAME: "sentinal-ml-worker"
    })
    
    provider = TracerProvider(resource=resource)
    exporter = OTLPSpanExporter(endpoint=settings.OTEL_EXPORTER_OTLP_ENDPOINT)
    provider.add_span_processor(BatchSpanProcessor(exporter))
    trace.set_tracer_provider(provider)

    # 2. Launch worker
    start_kafka_worker()