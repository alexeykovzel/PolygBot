package com.alexeykovzel.db.repository;

import com.alexeykovzel.db.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, String> {
    List<Chat> findByUserFirstName(String firstName);
}