package org.picstories.manager.parsers.repository;

import org.picstories.library.model.entity.comics.ComicsED;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

/**
 * @author arman.shamenov
 */
@Repository
public interface ComicsElasticsearchRepository extends ReactiveElasticsearchRepository<ComicsED, String> {
    Flux<ComicsED> findComicEDByLastCheckIsLessThanOrderByLastCheckAsc(LocalDateTime time);
}
