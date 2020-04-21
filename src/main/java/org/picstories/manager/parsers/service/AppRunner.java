package org.picstories.manager.parsers.service;

import org.picstories.library.model.kafka.parsers.UpdateTask;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import reactor.kafka.receiver.KafkaReceiver;

/**
 * @author arman.shamenov
 */
public class AppRunner implements ApplicationRunner {

    private final KafkaReceiver<String, UpdateTask> receiver;
    private final UpdateComicsService updateComicService;


    public AppRunner(KafkaReceiver<String, UpdateTask> receiver,
                     UpdateComicsService updateComicService) {
        this.receiver = receiver;
        this.updateComicService = updateComicService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        receiver.receive()
                .subscribe(updateComicService::updateComics);
    }
}
