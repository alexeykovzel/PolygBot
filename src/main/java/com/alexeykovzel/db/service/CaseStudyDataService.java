package com.alexeykovzel.db.service;

import com.alexeykovzel.PolygBotApplication;
import com.alexeykovzel.db.model.casestudy.CaseStudy;
import com.alexeykovzel.db.model.casestudy.CaseStudyId;
import com.alexeykovzel.db.repository.CaseStudyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@CacheConfig(cacheNames = {"value"})
public class CaseStudyDataService {
    private final Logger logger = PolygBotApplication.logger;
    private final CaseStudyRepository caseStudyRepository;

    public CaseStudyDataService(CaseStudyRepository caseStudyRepository) {
        this.caseStudyRepository = caseStudyRepository;
    }

    @Cacheable(value = "value", key = "#chatId")
    public Optional<List<String>> findTermValuesByChatId(String chatId) {
        logger.info("Spring caching hasn't been used..");
        return caseStudyRepository.findTermValuesByChatId(chatId);
    }

    public void save(CaseStudy caseStudy) {
        caseStudyRepository.save(caseStudy);
    }

    public Optional<CaseStudy> findById(Long termId, String chatId) {
        return caseStudyRepository.findById(new CaseStudyId(termId, chatId));
    }
}
