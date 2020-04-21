package org.picstories.manager.parsers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.elasticsearch.repository.config.EnableReactiveElasticsearchRepositories;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author arman.shamenov
 */
@SpringBootApplication
@EnableScheduling
@EnableReactiveMongoRepositories(basePackages = "org.picstories.library.repository")
@EnableReactiveElasticsearchRepositories
@EntityScan({
        "org.picstories.library.model.entity"
})
public class ParsersManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ParsersManagerApplication.class, args);
    }
}
