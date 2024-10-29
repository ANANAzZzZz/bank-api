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
public class BankTransactionControllerTests {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    @Transactional
    @Test
    public void testMakeBankTransaction() throws Exception {
        String request = "{\n" +
            "  \"amount\" : \"10000.50\",\n" +
            "  \"sender_card_number\" : \"4043444444444444\",\n" +
            "  \"receiver_card_number\" : \"4043111111111111\"\n" +
            "}";


        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/bankTransaction/commit")
                .content(request)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Transactional
    @Test
    public void testExceptionMakeBankTransaction() throws Exception {
        String request = "{\n" +
            "  \"amount\" : \"10000.50\",\n" +
            "  \"sender_card_number\" : \"404344444444444a\",\n" +
            "  \"receiver_card_number\" : \"4043111111111111\"\n" +
            "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/bankTransaction/commit")
                .content(request)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
