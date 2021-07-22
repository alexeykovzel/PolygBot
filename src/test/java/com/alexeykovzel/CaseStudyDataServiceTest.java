package com.alexeykovzel;

import com.alexeykovzel.db.repository.CaseStudyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@CacheConfig(cacheNames = {"value"})
public class CaseStudyDataServiceTest {

    @Autowired
    private CaseStudyRepository caseStudyRepository;

    @Cacheable(value = "value", key = "#chatId")
    public Optional<List<String>> findTermValuesByChatId(String chatId) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return caseStudyRepository.findTermValuesByChatId(chatId);
    }
}