package com.example.persistenceprogramming;

import com.example.persistenceprogramming.entity.Message;
import com.example.persistenceprogramming.repositories.MessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringDataConfiguration.class)
public class HelloWorldSpringDataJpaTest {

    @Autowired
    private MessageRepository messageRepository;

    @Test
    void storedLoadMessage() {
        final Message message = new Message();
        message.setText("Hello, Spring Data JPA!");

        messageRepository.save(message);

        final List<Message> messages = (List<Message>) messageRepository.findAll();

        assertAll(
                () -> assertEquals(1, messages.size()),
                () -> assertEquals("Hello, Spring Data JPA!", messages.get(0).getText())
        );

    }
}
