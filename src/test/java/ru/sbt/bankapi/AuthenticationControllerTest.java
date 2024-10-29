package ru.sbt.bankapi;

import org.springframework.transaction.annotation.Transactional;
import ru.sbt.bankapi.exceptions.CommonBankApiException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Objects;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTest {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    @Transactional
    @Test
    public void registerTest() throws Exception {
        String registerRequest = "{\"firstname\":\"John\",\"lastname\":\"Doe\",\"email\":\"john.doe@example.com\",\"password\":\"password123\", \"version\" : \"1\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                .content(registerRequest)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("3"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstname").value("John"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastname").value("Doe"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("john.doe@example.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.role").value("USER"));
    }

    @Transactional
    @Test
    public void authenticateTest() throws Exception {
        String authenticateRequest = "{\n" +
            "  \"password\" : \"111111\",\n" +
            "  \"email\" : \"example@mail.ru\"\n" +
            "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/authenticate")
                .content(authenticateRequest)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstname").value("Vladislav"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastname").value("Syvorotnev"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("example@mail.ru"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.role").value("USER"));
    }

    @Transactional
    @Test()
    public void authenticateWithWrongLoginTest() throws Exception {
        String authenticateRequest = "{\"email\":\"aexample@mail.ru\",\"password\":\"111111\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/authenticate")
                .content(authenticateRequest)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andExpect(result -> Assertions.assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(), CommonBankApiException.class));
    }
}



