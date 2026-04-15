package com.example.sentinal_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfig {

    @Bean
    public DefaultErrorHandler errorHandler(KafkaOperations<Object, Object> template) {
        // Recoverer that sends the failed message to <topic-name>.DLT
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template);
        
        // Configure retry: 3 attempts with a 1-second delay between them
        FixedBackOff backOff = new FixedBackOff(1000L, 3L);
        
        return new DefaultErrorHandler(recoverer, backOff);
    }

    @org.springframework.beans.factory.annotation.Value("${app.kafka.topic.transactions}")
    private String transactionsTopic;

    @org.springframework.beans.factory.annotation.Value("${app.kafka.topic.results}")
    private String resultsTopic;

    @Bean
    public org.apache.kafka.clients.admin.NewTopic transactionsTopicBean() {
        return org.springframework.kafka.config.TopicBuilder.name(transactionsTopic)
                .partitions(32)
                .replicas(1)
                .build();
    }

    @Bean
    public org.apache.kafka.clients.admin.NewTopic resultsTopicBean() {
        return org.springframework.kafka.config.TopicBuilder.name(resultsTopic)
                .partitions(32)
                .replicas(1)
                .build();
    }
}
