package com.poorbet.teams.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poorbet.teams.dto.TeamShortDto;
import com.poorbet.teams.dto.TeamStatsDto;
import com.poorbet.teams.request.TeamStatsRequest;
import com.poorbet.teams.service.TeamService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TeamController.class)
class TeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TeamService teamService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnTeamStats_whenRequestIsValid() throws Exception {
        UUID team1 = UUID.randomUUID();
        UUID team2 = UUID.randomUUID();

        TeamStatsRequest request = new TeamStatsRequest(List.of(team1, team2));

        List<TeamStatsDto> response = List.of(
                new TeamStatsDto(team1, "Inter Majami", 70, 65),
                new TeamStatsDto(team2, "Fc Barceluna", 60, 75)
        );

        when(teamService.getStatsByIds(request.teamIds())).thenReturn(response);

        mockMvc.perform(post("/api/teams/stats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(team1.toString()))
                .andExpect(jsonPath("$[0].name").value("Inter Majami"))
                .andExpect(jsonPath("$[0].attackPower").value(70))
                .andExpect(jsonPath("$[0].defencePower").value(65))
                .andExpect(jsonPath("$[1].id").value(team2.toString()))
                .andExpect(jsonPath("$[1].name").value("Fc Barceluna"))
                .andExpect(jsonPath("$[1].attackPower").value(60))
                .andExpect(jsonPath("$[1].defencePower").value(75));
    }

    @Test
    void shouldReturnNoContent_whenServiceReturnsEmptyList() throws Exception {
        UUID team1 = UUID.randomUUID();
        TeamStatsRequest request = new TeamStatsRequest(List.of(team1));

        when(teamService.getStatsByIds(request.teamIds())).thenReturn(List.of());

        mockMvc.perform(post("/api/teams/stats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnRandomTeams_whenCountProvided() throws Exception {
        when(teamService.findRandomTeams(3))
                .thenReturn(List.of(
                        new TeamStatsDto(UUID.randomUUID(), "Inter Miami", 11, 22),
                        new TeamStatsDto(UUID.randomUUID(), "Barcelona", 11, 22),
                        new TeamStatsDto(UUID.randomUUID(), "PSG", 11, 22)
                ));

        mockMvc.perform(get("/api/teams/random")
                        .param("count", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void shouldReturnOneTeam_whenCountNotProvided() throws Exception {
        when(teamService.findRandomTeams(1))
                .thenReturn(List.of(new TeamStatsDto(UUID.randomUUID(), "PSG", 11, 22)));

        mockMvc.perform(get("/api/teams/random"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldReturn400_whenCountIsZero() throws Exception {
        mockMvc.perform(get("/api/teams/random")
                        .param("count", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400_whenCountIsNegative() throws Exception {
        mockMvc.perform(get("/api/teams/random")
                        .param("count", "-5"))
                .andExpect(status().isBadRequest());
    }

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
