package com.alexeykovzel.db.service;

import com.alexeykovzel.PolygBotApplication;
import com.alexeykovzel.db.model.term.Term;
import com.alexeykovzel.db.repository.TermRepository;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class TermDataService {
    private final Logger logger = PolygBotApplication.logger;
    private final TermRepository termRepository;

    public TermDataService(TermRepository termRepository) {
        this.termRepository = termRepository;
    }

    Term findByValue(String value) {
        return termRepository.findByValue(value);
    }

    boolean existsByValue(String value) {
        return termRepository.existsByValue(value);
    }

    Term findDetailedById(Long id) {
        return termRepository.findDetailedById(id);
    }

    Long findIdByValue(String value) {
        return termRepository.findIdByValue(value);
    }
}
