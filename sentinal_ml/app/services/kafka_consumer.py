from kafka import KafkaConsumer, KafkaProducer
import json
from app.services.predictor import predictor
from app.core.config import settings

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
        
        # 2. Extract features without making predictions yet
        for tp, messages in msg_pack.items():
            for message in messages:
                data = message.value
                batch_tx_ids.append(data.get('id'))
                batch_features.append(data.get('features'))
                
        if batch_features:
            print(f"🧠 Analyzing Batch of {len(batch_tx_ids)} Transactions...")
            
            # 3. Vectorized Prediction
            predictions = predictor.predict(batch_features)
            
            # 4. Asynchronous Produce
            for i, tx_id in enumerate(batch_tx_ids):
                response = {
                    "transactionId": tx_id,
                    "is_fraud": predictions[i]['is_fraud'],
                    "confidence": predictions[i]['confidence']
                }
                # .send() is async!
                producer.send(settings.TOPIC_RESULTS, value=response)
                
            # 5. Flush the whole batch at once 
            producer.flush()
            
            # 6. Mark the Kafka offset as officially processed
            consumer.commit()
            print(f"✅ Successfully processed batch of {len(batch_tx_ids)} transactions.")
