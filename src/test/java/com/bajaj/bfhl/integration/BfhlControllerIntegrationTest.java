package com.bajaj.bfhl.integration;

import com.bajaj.bfhl.dto.BfhlRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for POST /bfhl endpoint.
 * Uses @SpringBootTest to spin up the full Spring context.
 */
@SpringBootTest
@AutoConfigureMockMvc
class BfhlControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // =====================================================================
    // EXAMPLE 1 Integration Test
    // =====================================================================
    @Test
    @DisplayName("POST /bfhl - Example 1: basic categorization")
    void integrationTest_example1() throws Exception {
        BfhlRequest request = new BfhlRequest(Arrays.asList("A", "1", "22", "$", "B", "7"));

        mockMvc.perform(post("/bfhl")
                        .header("X-Request-Id", "REQ-1001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_success").value(true))
                .andExpect(jsonPath("$.request_id").value("REQ-1001"))
                .andExpect(jsonPath("$.sum").value("30"))
                .andExpect(jsonPath("$.largest_number").value("22"))
                .andExpect(jsonPath("$.smallest_number").value("1"))
                .andExpect(jsonPath("$.alphabet_count").value(2))
                .andExpect(jsonPath("$.number_count").value(3))
                .andExpect(jsonPath("$.special_character_count").value(1))
                .andExpect(jsonPath("$.contains_duplicates").value(false))
                .andExpect(jsonPath("$.odd_numbers", containsInAnyOrder("1", "7")))
                .andExpect(jsonPath("$.even_numbers", contains("22")))
                .andExpect(jsonPath("$.alphabets", containsInAnyOrder("A", "B")))
                .andExpect(jsonPath("$.special_characters", contains("$")))
                .andExpect(jsonPath("$.processing_time_ms", notNullValue()));
    }

    // =====================================================================
    // EXAMPLE 3 Integration Test – Duplicates + null + empty
    // =====================================================================
    @Test
    @DisplayName("POST /bfhl - Example 3: duplicates, null, empty")
    void integrationTest_example3() throws Exception {
        String body = "{\"data\": [\"10\", \"10\", \"A\", \"A\", \"\", null, \"&\", \"5\"]}";

        mockMvc.perform(post("/bfhl")
                        .header("X-Request-Id", "REQ-1003")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_success").value(true))
                .andExpect(jsonPath("$.request_id").value("REQ-1003"))
                .andExpect(jsonPath("$.contains_duplicates").value(true))
                .andExpect(jsonPath("$.sum").value("15"))
                .andExpect(jsonPath("$.alphabet_count").value(1))
                .andExpect(jsonPath("$.number_count").value(2))
                .andExpect(jsonPath("$.summary.total_elements_received").value(8))
                .andExpect(jsonPath("$.summary.invalid_elements_ignored").value(2));
    }

    // =====================================================================
    // EXAMPLE 4 Integration Test – Negative and decimal numbers
    // =====================================================================
    @Test
    @DisplayName("POST /bfhl - Example 4: negative and decimal numbers")
    void integrationTest_example4() throws Exception {
        BfhlRequest request = new BfhlRequest(Arrays.asList("-10", "25.5", "-100.75", "B", "@", "5", "A9"));

        mockMvc.perform(post("/bfhl")
                        .header("X-Request-Id", "REQ-1004")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_success").value(true))
                .andExpect(jsonPath("$.largest_number").value("25.5"))
                .andExpect(jsonPath("$.smallest_number").value("-100.75"))
                .andExpect(jsonPath("$.sum").value("-80.25"))
                .andExpect(jsonPath("$.contains_duplicates").value(false));
    }

    // =====================================================================
    // Missing X-Request-Id header → defaults to "UNKNOWN"
    // =====================================================================
    @Test
    @DisplayName("POST /bfhl - missing X-Request-Id defaults to UNKNOWN")
    void integrationTest_missingRequestId() throws Exception {
        BfhlRequest request = new BfhlRequest(List.of("A", "1"));

        mockMvc.perform(post("/bfhl")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.request_id").value("UNKNOWN"));
    }

    // =====================================================================
    // Null data field → 400 Bad Request
    // =====================================================================
    @Test
    @DisplayName("POST /bfhl - null data field returns 400")
    void integrationTest_nullDataReturns400() throws Exception {
        String body = "{\"data\": null}";

        mockMvc.perform(post("/bfhl")
                        .header("X-Request-Id", "REQ-NULL")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.is_success").value(false));
    }

    // =====================================================================
    // Malformed JSON → 400 Bad Request
    // =====================================================================
    @Test
    @DisplayName("POST /bfhl - malformed JSON returns 400")
    void integrationTest_malformedJson() throws Exception {
        mockMvc.perform(post("/bfhl")
                        .header("X-Request-Id", "REQ-BAD")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid json}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.is_success").value(false));
    }

    // =====================================================================
    // Empty data array → valid response with zeroes
    // =====================================================================
    @Test
    @DisplayName("POST /bfhl - empty data array returns success with zero counts")
    void integrationTest_emptyData() throws Exception {
        BfhlRequest request = new BfhlRequest(List.of());

        mockMvc.perform(post("/bfhl")
                        .header("X-Request-Id", "REQ-EMPTY")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_success").value(true))
                .andExpect(jsonPath("$.alphabet_count").value(0))
                .andExpect(jsonPath("$.number_count").value(0))
                .andExpect(jsonPath("$.sum").value("0"));
    }
}
