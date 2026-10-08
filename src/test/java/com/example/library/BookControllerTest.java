package com.example.library;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateQueryUpdateAndDeleteBook() throws Exception {
        String token = login("admin", "admin123");
        String createBody = """
                {
                  "title": "Spring Boot实战",
                  "author": "Craig Walls",
                  "isbn": "9787115527929",
                  "publisher": "人民邮电出版社",
                  "publishYear": 2020,
                  "totalCopies": 4,
                  "availableCopies": 4
                }
                """;

        mockMvc.perform(post("/api/books")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Spring Boot实战"));

        mockMvc.perform(get("/api/books")
                        .header("Authorization", "Bearer " + token)
                        .param("keyword", "Spring"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].isbn").value("9787115527929"));

        String updateBody = """
                {
                  "title": "Spring Boot实战",
                  "author": "Craig Walls",
                  "isbn": "9787115527929",
                  "publisher": "人民邮电出版社",
                  "publishYear": 2020,
                  "totalCopies": 4,
                  "availableCopies": 3
                }
                """;

        mockMvc.perform(put("/api/books/1")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableCopies").value(3));

        mockMvc.perform(delete("/api/books/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldRejectInvalidCopies() throws Exception {
        String token = login("admin", "admin123");
        String body = """
                {
                  "title": "错误数据",
                  "author": "测试",
                  "isbn": "invalid-copies",
                  "publisher": "测试出版社",
                  "publishYear": 2024,
                  "totalCopies": 1,
                  "availableCopies": 2
                }
                """;

        mockMvc.perform(post("/api/books")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Available copies cannot exceed total copies"));
    }

    @Test
    void shouldRejectWriteFromReader() throws Exception {
        String token = login("reader", "reader123");
        String body = """
                {
                  "title": "Reader Write",
                  "author": "Reader",
                  "isbn": "reader-write",
                  "publisher": "Test",
                  "publishYear": 2024,
                  "totalCopies": 1,
                  "availableCopies": 1
                }
                """;

        mockMvc.perform(post("/api/books")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    private String login(String username, String password) throws Exception {
        String body = """
                {
                  "username": "%s",
                  "password": "%s"
                }
                """.formatted(username, password);
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andReturn();
        String json = result.getResponse().getContentAsString();
        return json.replaceAll(".*\\\"token\\\"\\s*:\\s*\\\"([^\\\"]+)\\\".*", "$1");
    }
}
