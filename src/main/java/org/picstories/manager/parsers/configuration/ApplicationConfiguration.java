package org.picstories.manager.parsers.configuration;

import org.picstories.library.elasticsearch.ReactiveElasticsearchConfiguration;
import org.picstories.library.mapper.PicstoriesMapper;
import org.picstories.library.model.kafka.parsers.UpdateTask;
import org.picstories.library.mongodb.ReactiveMongoDbConfiguration;
import org.picstories.manager.parsers.service.AppRunner;
import org.picstories.manager.parsers.service.UpdateComicsService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.ReactiveMongoTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.kafka.receiver.KafkaReceiver;

/**
 * @author arman.shamenov
 */
@Configuration
@Import({
        ReactiveElasticsearchConfiguration.class,
        ReactiveMongoDbConfiguration.class
})
public class ApplicationConfiguration {

    @Bean
    public PicstoriesMapper picstoriesMapper() {
        return new PicstoriesMapper();
    }

    @Bean
    public ReactiveTransactionManager rtm(@Qualifier("reactiveMongoDbFactory") ReactiveMongoDatabaseFactory dbf) {
        return new ReactiveMongoTransactionManager(dbf);
    }

    @Bean
    public TransactionalOperator tm(ReactiveTransactionManager rtm) {
        return TransactionalOperator.create(rtm);
    }

    @Bean
    public ApplicationRunner applicationRunner(
            KafkaReceiver<String, UpdateTask> kafkaReceiver,
            UpdateComicsService updateService
    ) {
        return new AppRunner(kafkaReceiver, updateService);
    }
}
