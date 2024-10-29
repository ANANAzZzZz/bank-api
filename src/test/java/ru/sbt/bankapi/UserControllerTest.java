package ru.sbt.bankapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    @Test
    @Transactional
    public void testGetAllUsers() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Transactional
    public void testGetUserById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/1")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstname").value("Vladislav"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastname").value("Syvorotnev"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("example@mail.ru"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.role").value("USER"));
    }

    @Test
    @Transactional
    public void testUserUpdate() throws Exception {
        String updateUserRequest = "{\n" +
            "  \"id\" : \"1\",\n" +
            "  \"firstname\" : \"updated1\",\n" +
            "  \"lastname\" : \"updated2\",\n" +
            "  \"password\" : \"111111\",\n" +
            "  \"email\" : \"updated@mail.ru\",\n" +
            "  \"role\" : \"USER\"\n" +
            "}";

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users")
                .content(updateUserRequest)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstname").value("updated1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastname").value("updated2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("updated@mail.ru"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.role").value("USER"));
    }

    @Test
    @Transactional
    public void testUserDelete() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/users/2")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
