from kafka import KafkaConsumer, KafkaProducer
import json
from app.services.predictor import predictor
from app.core.config import settings

from opentelemetry import trace
from opentelemetry.trace import Link
from opentelemetry.propagate import extract, inject

tracer = trace.get_tracer(__name__)

def start_kafka_worker():
    consumer = KafkaConsumer(
        settings.TOPIC_TRANSACTIONS,
        bootstrap_servers=[settings.KAFKA_SERVERS],
        auto_offset_reset='earliest',
        enable_auto_commit=False,
        group_id=settings.CONSUMER_GROUP, # Change the name slightly to reset offset
        value_deserializer=lambda m: json.loads(m.decode('utf-8'))
    )

    producer = KafkaProducer(
        bootstrap_servers=[settings.KAFKA_SERVERS],
        value_serializer=lambda v: json.dumps(v).encode('utf-8')
    )

    print("🚀 Sentinal AI Worker is live and batch processing...")
    
    while True:
        # 1. Grab a batch of up to 500 messages at once
        msg_pack = consumer.poll(timeout_ms=1000, max_records=500)
        
        if not msg_pack:
            continue
            
        batch_tx_ids = []
        batch_features = []
        batch_contexts = []
        links = []
        
        # 2. Extract features without making predictions yet
        for tp, messages in msg_pack.items():
            for message in messages:
                data = message.value
                batch_tx_ids.append(data.get('id'))
                batch_features.append(data.get('features'))
                
                # Context Extraction
                headers = {k: v.decode('utf-8') for k, v in message.headers} if message.headers else {}
                ctx = extract(headers)
                batch_contexts.append(ctx)
                
                # Jaeger System Architecture relies EXCLUSIVELY on Parent-Child traces with SpanKind.CONSUMER/SERVER
                # By creating this brief CONSUMER span using the extracted context, we instantly force Jaeger
                # to draw a connecting edge from `sentinal-backend` to `sentinal-ml-worker`!
                with tracer.start_as_current_span(
                    "kafka_receive",
                    context=ctx,
                    kind=trace.SpanKind.CONSUMER
                ) as consume_span:
                    consume_span.set_attribute("messaging.system", "kafka")
                    consume_span.set_attribute("messaging.destination", settings.TOPIC_TRANSACTIONS)
                    
                # Setup Linking for vectorized inference batch grouping
                current_span = trace.get_current_span(ctx)
                if current_span and current_span.get_span_context().is_valid:
                     links.append(Link(current_span.get_span_context()))
                
        if batch_features:
            print(f"🧠 Analyzing Batch of {len(batch_tx_ids)} Transactions...")
            
            with tracer.start_as_current_span("xgboost_batch_inference", links=links):
                # 3. Vectorized Prediction
                predictions = predictor.predict(batch_features)
            
            # 4. Asynchronous Produce
            for i, tx_id in enumerate(batch_tx_ids):
                response = {
                    "transactionId": tx_id,
                    "is_fraud": predictions[i]['is_fraud'],
                    "confidence": predictions[i]['confidence']
                }
                
                out_headers = {}
                inject(out_headers, context=batch_contexts[i])
                kafka_headers = [(k, v.encode('utf-8')) for k, v in out_headers.items()]
                
                # .send() is async!
                producer.send(settings.TOPIC_RESULTS, value=response, headers=kafka_headers)
                
            # 5. Flush the whole batch at once 
            producer.flush()
            
            # 6. Mark the Kafka offset as officially processed
            consumer.commit()
            print(f"✅ Successfully processed batch of {len(batch_tx_ids)} transactions.")
