package com.alexeykovzel.db.service;

import com.alexeykovzel.PolygBotApplication;
import com.alexeykovzel.db.model.Chat;
import com.alexeykovzel.db.repository.ChatRepository;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatDataService {
    private final Logger logger = PolygBotApplication.logger;
    private final ChatRepository chatRepository;

    public ChatDataService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    List<Chat> findByUserFirstName(String firstName) {
        return chatRepository.findByUserFirstName(firstName);
    }
}
