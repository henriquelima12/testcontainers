package com.example.testcontainers;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.example.testcontainers.entities.Task;
import com.example.testcontainers.enums.Status;
import com.example.testcontainers.repositories.TaskRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TaskRepositoryTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.2.0");

    @Autowired
    TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        Task task = new Task();
        task.setId(16l);
        task.setName("teste");
        task.setStatus(Status.DONE);
        taskRepository.save(task);
    }

    @Test
    void connectionEstablished() {
        assertThat(mysql.isCreated()).isTrue();
        assertThat(mysql.isRunning()).isTrue();
    }

    @Test
    void shouldReturnPostByTitle() {
        Task task = taskRepository.findByName("teste").orElseThrow();
        assertEquals("teste", task.getName(), "Task name should be 'teste'");
    }

    @Test
    void shouldNotReturnPostWhenTitleIsNotFound() {
        Optional<Task> task = taskRepository.findByName("Wrong name");
        assertFalse(task.isPresent(), "Task should not be present");
    }

}