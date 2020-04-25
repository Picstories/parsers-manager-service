package org.picstories.manager.parsers.service;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.picstories.library.model.entity.comics.ComicsED;
import org.picstories.library.model.kafka.parsers.ParseTask;
import org.picstories.manager.parsers.repository.ComicsElasticsearchRepository;
import org.picstories.manager.parsers.repository.PageElasticsearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author arman.shamenov
 */
@Service
public class ScheduledParseService {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledParseService.class);

    @Value("${spring.kafka.template.default-topic}")
    private String topic;

    private static final int ONE_MINUTE = 1000 * 60;

    private final ComicsElasticsearchRepository comicsRepos;
    private final PageElasticsearchRepository pageRepos;

    private final KafkaSender<String, ParseTask> producer;

    public ScheduledParseService(ComicsElasticsearchRepository comicsRepos,
                                 PageElasticsearchRepository pageRepos,
                                 KafkaSender<String, ParseTask> producer) {
        this.comicsRepos = comicsRepos;
        this.pageRepos = pageRepos;
        this.producer = producer;
    }

    @Scheduled(initialDelay = ONE_MINUTE * 10, fixedDelay = ONE_MINUTE * 1440)
    public void sendParserTaskMessage() {
        logger.info("Start produce messages to kafka in time = {}", LocalDateTime.now());
        comicsRepos
                .findComicEDByLastCheckIsLessThanOrderByLastCheckAsc(LocalDateTime.now().minusDays(1))
                .doOnError(err -> logger.error("Error while get comic ", err))
                .flatMap(comics -> {
                    comics.setLastCheck(LocalDateTime.now());
                    return producer.send(recordMono(comics));
                })
                .doOnError(err -> logger.error("Fail to send parser task to kafka  ", err))
                .doOnNext(senderResult -> logger.info("Success send to kafka record = {} in the topic = {} ",
                        senderResult.toString(), senderResult.recordMetadata().topic()))
                .collectList()
                .subscribe(it -> logger.info("Stop produce messages to kafka"));
    }

    private Mono<SenderRecord<String, ParseTask, String>> recordMono(ComicsED comic) {
        String lastPageId = comic.getId() + "_" + comic.getPageCount();
        return pageRepos.findById(lastPageId)
                .map(page -> {
                    UUID uuid = UUID.randomUUID();
                    ParseTask parserTaskMessage = new ParseTask(comic, page);

                    ProducerRecord<String, ParseTask> producerRecord = new ProducerRecord<>(topic, parserTaskMessage);
                    return SenderRecord.create(producerRecord, "task  id = " + uuid.toString() + " with data = " + parserTaskMessage.toString());
                })
                .doOnNext(record -> logger.info("Record = {}", record.toString()))
                .doOnError(err -> logger.error("Error creating record ", err))
                .take(Duration.ofSeconds(6));
    }
}
