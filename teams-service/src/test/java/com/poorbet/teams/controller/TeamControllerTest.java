package com.poorbet.teams.controller;

import com.poorbet.teams.dto.TeamShortDto;
import com.poorbet.teams.service.TeamService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(TeamController.class)
class TeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TeamService teamService;

    @MockitoBean
    private CacheManager cacheManager;

    @Test
    void shouldReturnTeam_whenIdExists() throws Exception {
        UUID id = UUID.randomUUID();
        String name = "FC Barceluna";
        TeamShortDto team = new TeamShortDto(id, name);

        when(teamService.getById(id)).thenReturn(team);

        mockMvc.perform(get("/api/teams/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value(name));
    }

    @Test
    void shouldReturn400_whenIdIsNotUuid() throws Exception {
        mockMvc.perform(get("/api/teams/{id}", "not-a-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404_whenTeamNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        when(teamService.getById(id))
                .thenThrow(new EntityNotFoundException("Team not found"));

        mockMvc.perform(get("/api/teams/{id}", id))
                .andExpect(status().isNotFound());
    }
}
