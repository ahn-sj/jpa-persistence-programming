package com.example.persistenceprogramming;

import com.example.persistenceprogramming.entity.Message;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PersistenceProgrammingApplicationTests {

    @Test
    void storeLoadMessage() {

        // emf는 thread-safe하다.
        final EntityManagerFactory emf = Persistence.createEntityManagerFactory("ch02");

        try {
            // em -> DB 세션 생성 == 영속성 컨텍스트
            final EntityManager em = emf.createEntityManager();

            em.getTransaction().begin();
            final Message message = new Message();
            message.setText("Hello, JPA!");
            em.persist(message);
            em.getTransaction().commit();

            em.getTransaction().begin();
            final List<Message> messages = em.createQuery("SELECT m FROM Message m", Message.class)
                    .getResultList();
            messages.get(messages.size() - 1).setText("Hello, JPA! Updated!");
            em.getTransaction().commit();

            assertAll(
                    () -> assertEquals(1, messages.size()),
                    () -> assertEquals("Hello, JPA! Updated!", messages.get(0).getText()));
            em.close();
        } finally {
            emf.close();
        }
    }

}
