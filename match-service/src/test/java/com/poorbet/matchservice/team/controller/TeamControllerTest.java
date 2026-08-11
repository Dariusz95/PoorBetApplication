package com.poorbet.matchservice.team.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poorbet.matchservice.security.CurrentUserProvider;
import com.poorbet.matchservice.team.dto.IncreaseTeamPowerDto;
import com.poorbet.matchservice.team.dto.PowerType;
import com.poorbet.matchservice.team.dto.TeamResponse;
import com.poorbet.matchservice.team.dto.TeamShortDto;
import com.poorbet.matchservice.team.service.TeamService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static com.poorbet.matchservice.fixture.TeamFixtures.BARCELONA;
import static com.poorbet.matchservice.fixture.TeamFixtures.BARCELONA_ID;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(com.poorbet.matchservice.team.controller.TeamController.class)
class TeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TeamService teamService;

    @MockitoBean
    private CacheManager cacheManager;

    @MockitoBean
    private CurrentUserProvider currentUserProvider;

    @Test
    void shouldReturnTeam_whenIdExists() throws Exception {
        UUID id = BARCELONA_ID;
        String name = BARCELONA;
        TeamShortDto team = new TeamShortDto(id, name, "");

        when(teamService.getById(id)).thenReturn(team);

        mockMvc.perform(get("/api/teams/public/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value(name));
    }

    @Test
    void shouldReturn400_whenIdIsNotUuid() throws Exception {
        mockMvc.perform(get("/api/teams/public/{id}", "not-a-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404_whenTeamNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        when(teamService.getById(id))
                .thenThrow(new EntityNotFoundException("Team not found"));

        mockMvc.perform(get("/api/teams/public/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void increasePower_shouldReturnUpdatedTeam_whenRequestValid() throws Exception {
        UUID userId = UUID.randomUUID();
        TeamResponse response = TeamResponse.builder()
                .id(BARCELONA_ID)
                .name(BARCELONA)
                .attackPower(86)
                .defencePower(72)
                .build();

        when(currentUserProvider.getUserId()).thenReturn(userId);
        when(teamService.increasePower(new IncreaseTeamPowerDto(PowerType.ATTACK), userId)).thenReturn(response);

        mockMvc.perform(post("/api/teams/public/power")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new IncreaseTeamPowerDto(PowerType.ATTACK))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attackPower").value(86));
    }

    @Test
    void increasePower_shouldReturn400_whenPowerTypeMissing() throws Exception {
        mockMvc.perform(post("/api/teams/public/power")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
