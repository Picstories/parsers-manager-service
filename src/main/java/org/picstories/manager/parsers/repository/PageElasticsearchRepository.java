package org.picstories.manager.parsers.repository;

import org.picstories.library.model.entity.page.PageED;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author arman.shamenov
 */
@Repository
public interface PageElasticsearchRepository extends ReactiveElasticsearchRepository<PageED, String> {
}
