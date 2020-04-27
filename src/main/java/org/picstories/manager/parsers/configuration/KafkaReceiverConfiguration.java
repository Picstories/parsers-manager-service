package org.picstories.manager.parsers.configuration;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.picstories.library.model.kafka.parsers.UpdateTask;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadFactory;
import java.util.function.Supplier;

/**
 * @author arman.shamenov
 */
@Configuration
public class KafkaReceiverConfiguration {
    @Value("${spring.kafka.bootstrap-servers}")
    private String serverHost;
    @Value("${tpd.consume-topic}")
    private String topic;
    @Value("${spring.kafka.consumer.group-id}")
    private String consumeGroupId;

    @Bean
    public KafkaReceiver<String, UpdateTask> kafkaReceiver() {
        JsonDeserializer<UpdateTask> deserializer = new JsonDeserializer<>(UpdateTask.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, serverHost);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumeGroupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        ThreadFactory factory = new ThreadFactoryBuilder()
                .setNameFormat("kafka-consumer-thread-%d")
                .setDaemon(false)
                .build();
        Supplier<Scheduler> schedulerSupplier = () -> Schedulers.newElastic(95, factory);

        ReceiverOptions<String, UpdateTask> receiverOptions = ReceiverOptions
                .<String, UpdateTask>create(props)
                .schedulerSupplier(schedulerSupplier)
                .withValueDeserializer(deserializer)
                .subscription(Collections.singletonList(topic));

        return KafkaReceiver.create(receiverOptions);
    }
}
