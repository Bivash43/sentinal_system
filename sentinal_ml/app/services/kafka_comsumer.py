from kafka import KafkaConsumer, KafkaProducer
from app.core.config import settings
import json
from app.services.predictor import predictor


def start_kafka_worker():
    # 1. Set up the Consumer (Listener)
    consumer = KafkaConsumer(
        settings.TOPIC_TRANSACTIONS,
        bootstrap_servers=settings.KAFKA_SERVERS,
        value_deserializer=lambda m: json.loads(m.decode('utf-8'))
    )

    # 2. Set up the Producer (To send results back)
    producer = KafkaProducer(
        bootstrap_servers=[settings.KAFKA_SERVERS, ],
        value_serializer=lambda v: json.dumps(v).encode('utf-8')
    )

    print("🚀 Sentinel Kafka Worker is listening for transactions...")

    for message in consumer:
        transaction_data = message.value
        # Assuming Java sends: {"transactionId": "123", "features": [...]}
        features = transaction_data.get('features')

        # Run AI Prediction
        result = predictor.predict(features)

        # Add the original ID so Java knows which transaction this is
        result['transactionId'] = transaction_data.get('transactionId')

        # Send a result back to Java
        producer.send('fraud_results', value=result)
        print(f"Processed ID {result['transactionId']}: Fraud={result['is_fraud']}")
