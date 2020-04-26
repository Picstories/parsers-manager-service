package org.picstories.manager.parsers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author arman.shamenov
 */
@SpringBootApplication
@EnableScheduling
@EntityScan({
        "org.picstories.library.model.entity"
})
public class ParsersManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ParsersManagerApplication.class, args);
    }
}
