package org.picstories.manager.parsers.configuration;

import org.picstories.library.elasticsearch.ReactiveElasticsearchConfiguration;
import org.picstories.library.mapper.PicstoriesMapper;
import org.picstories.library.mongodb.ReactiveMongoDbConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

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
}
