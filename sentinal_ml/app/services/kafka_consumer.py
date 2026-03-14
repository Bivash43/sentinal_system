from kafka import KafkaConsumer, KafkaProducer
import json
from app.services.predictor import predictor
from app.core.config import settings

def start_kafka_worker():
    consumer = KafkaConsumer(
        settings.TOPIC_TRANSACTIONS,
        bootstrap_servers=[settings.KAFKA_SERVERS],
        auto_offset_reset='earliest',
        group_id=settings.CONSUMER_GROUP, # Change the name slightly to reset offset
        value_deserializer=lambda m: json.loads(m.decode('utf-8'))
    )

    producer = KafkaProducer(
        bootstrap_servers=[settings.KAFKA_SERVERS],
        value_serializer=lambda v: json.dumps(v).encode('utf-8')
    )

    print("🚀 Sentinel AI Worker is live and processing...")
    for message in consumer:
        data = message.value
        tx_id = data.get('id')  # Matches Java "id"
        features = data.get('features')

        if features:
            print(f"🧠 Analyzing Transaction: {tx_id}")
            
            # 1. Run Prediction
            prediction_result = predictor.predict(features)
            
            # 2. Format Response for Java
            # We must include the 'transactionId' key because your Java 
            # FraudResultConsumer looks for that key!
            response = {
                "transactionId": tx_id,
                "is_fraud": int(prediction_result['is_fraud']),
                "confidence": float(prediction_result['confidence'])
            }

            # 3. Send back to Java
            producer.send(settings.TOPIC_RESULTS, value=response)
            producer.flush()
            print(f"✅ Result sent for {tx_id}: Fraud={response['is_fraud']}")