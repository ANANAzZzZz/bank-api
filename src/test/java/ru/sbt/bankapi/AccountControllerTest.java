package ru.sbt.bankapi;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import ru.sbt.bankapi.exceptions.CommonBankApiException;

import java.util.Objects;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    @Transactional
    @Test
    public void testGetAllAccounts() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Transactional
    @Test
    public void testGetAccountById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/accounts/1")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value("1"));
    }

    @Transactional
    @Test
    public void testGetAccountByIdException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/accounts/100")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andExpect(result -> Assertions.assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(), CommonBankApiException.class));
    }

    @Transactional
    @Test
    public void testAccountCreate() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/accounts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("3"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value("1"));
    }

    @Transactional
    @Test
    public void testAccountCreateException() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/accounts/100")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andExpect(result -> Assertions.assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(), CommonBankApiException.class));
    }

    @Transactional
    @Test
    public void testAccountDelete() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/accounts/2")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Transactional
    @Test
    public void testAccountDeleteException() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/accounts/100")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
