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
public class CardControllerTest {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    @Transactional
    @Test
    public void testGetAllCards() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cards")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Transactional
    @Test
    public void testGetCardBalance() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cards/balance/1")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.balance").value("10000.5"));
    }

    @Transactional
    @Test
    public void testTopUpBalance() throws Exception {
        String request = "{\n" +
            "  \"cardNumber\": \"4043444444444444\",\n" +
            "  \"amount\" : \"1000\"\n" +
            "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/cards/topUpBalance")
                .content(request)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.number").value("4043444444444444"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.balance").value("11000.5"));
    }

    @Transactional
    @Test
    public void testTopUpBalanceException() throws Exception {
        String request = "{\n" +
            "  \"cardNumber\": \"4043444444444443\",\n" +
            "  \"amount\" : \"1000\"\n" +
            "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/cards/topUpBalance")
                .content(request)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }


    @Transactional
    @Test
    public void testCreateCardByAccountId() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/cards/1")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Transactional
    @Test
    public void testCreateCardByAccountIdException() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/cards/100")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(result -> Assertions.assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(), CommonBankApiException.class));
    }

    @Transactional
    @Test
    public void testDeleteCardByAccountId() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/cards/1")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Transactional
    @Test
    public void testDeleteCardByAccountIdException() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/cards/100")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
