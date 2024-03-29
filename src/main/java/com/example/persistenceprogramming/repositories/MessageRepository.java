package com.example.persistenceprogramming.repositories;

import com.example.persistenceprogramming.entity.Message;
import org.springframework.data.repository.CrudRepository;

public interface MessageRepository extends CrudRepository<Message, Long> {
}
