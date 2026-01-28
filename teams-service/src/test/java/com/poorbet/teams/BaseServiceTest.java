package com.poorbet.teams;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.poorbet.teams.mapper.TeamMapper;
import com.poorbet.teams.repository.TeamRepository;

@ExtendWith(MockitoExtension.class)
public abstract class BaseServiceTest {

    @Mock
    protected TeamRepository teamRepository;

    @Mock
    protected TeamMapper teamMapper;

    @BeforeEach
    protected abstract void setUp();
}
