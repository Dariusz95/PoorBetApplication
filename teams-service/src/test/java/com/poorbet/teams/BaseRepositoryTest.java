package com.poorbet.teams;

import java.util.List;

import com.poorbet.teams.model.Team;
import com.poorbet.teams.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@DataJpaTest
@ActiveProfiles("test")
public abstract class BaseRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("teams_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    protected TeamRepository teamRepository;

    @BeforeEach
    void setUp() {
        teamRepository.deleteAll();
        initializeTestData();
    }

    protected void initializeTestData() {
        List<Team> teams = List.of(
                TeamFixtures.manchesterUnited(),
                TeamFixtures.liverpool(),
                TeamFixtures.chelsea(),
                TeamFixtures.interMiami(),
                TeamFixtures.barcelona(),
                TeamFixtures.psg()
        );
        teamRepository.saveAll(teams);
    }
}
