package com.alexeykovzel;

import com.alexeykovzel.db.repository.CaseStudyRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan
@TestPropertySource(
        locations = "classpath:application.properties")
public class CacheConfigTest {
    private static final Logger logger = LoggerFactory.getLogger(CacheConfigTest.class);

    @Autowired
    private CaseStudyDataServiceTest caseStudyDataService;

    @Autowired
    private CacheManager cacheManager;

    @Test
    void checkCashing() {
        String chatId = "597554184";

        for (int i = 0; i < 5; i++) {
            caseStudyDataService.findTermValuesByChatId(chatId).ifPresent(termValues -> {
                for (String value : termValues) {
                    System.out.println("value: " + value);
                }
            });
        }
    }
}
