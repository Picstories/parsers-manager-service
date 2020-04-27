package org.picstories.manager.parsers.service;

import com.google.common.collect.Iterables;
import com.mongodb.MongoClientException;
import org.picstories.library.model.entity.comics.Comics;
import org.picstories.library.model.entity.comics.ComicsMD;
import org.picstories.library.model.entity.page.PageMD;
import org.picstories.library.model.kafka.parsers.UpdateTask;
import org.picstories.library.repository.mongo.ComicsMongoRepository;
import org.picstories.library.repository.mongo.PageMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author arman.shamenov
 */
@Service
public class UpdateComicsService implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(UpdateComicsService.class);

    private final PageMongoRepository pageRepos;
    private final ComicsMongoRepository comicsRepos;

    private final KafkaReceiver<String, UpdateTask> receiver;

    public UpdateComicsService(PageMongoRepository pageRepos,
                               ComicsMongoRepository comicsRepos,
                               KafkaReceiver<String, UpdateTask> receiver) {
        this.pageRepos = pageRepos;
        this.comicsRepos = comicsRepos;
        this.receiver = receiver;
    }

    @Transactional(rollbackFor = MongoClientException.class)
    public Mono<ComicsMD> updateComics(ReceiverRecord<String, UpdateTask> record) {
        UpdateTask task = record.value();

        List<PageMD> pageList = task.getPages().stream().map(PageMD::new).collect(Collectors.toList());
        Comics comic = task.getComics();
        PageMD lastPage = Iterables.getLast(pageList);
        comic.setLastUpdate(LocalDateTime.now());
        comic.setPageCount(lastPage.getNumber());

        return pageRepos.saveAll(pageList)
                .doOnNext(page -> logger.info("page save = {} ", page))
                .doOnError(err -> logger.error("Error saving page ", err))
                .then(comicsRepos.save(new ComicsMD(comic)))
                .doOnNext(com -> logger.info("comic save = {}", com))
                .doOnError(com -> logger.error("error saving comic ", com));
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        receiver.receive()
                .flatMap(this::updateComics)
                .doOnError(error -> logger.error("Error while execute in transaction update comics : ", error))
                .subscribe();
    }
}
