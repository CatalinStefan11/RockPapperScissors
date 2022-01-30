package com.rockpaperscissors.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rockpaperscissors.exception.model.ClientError;
import com.rockpaperscissors.model.entities.GameSession;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.text.SimpleDateFormat;
import java.util.Arrays;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    static void init() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        mapper.registerModule(new JavaTimeModule());
        mapper.setDateFormat(df);

    }

    @Test
    @Sql("/sql/insertPlayer2.sql")
    public void createGame_withSuccess() throws Exception {

        MvcResult storyResult = mockMvc.perform(MockMvcRequestBuilders
                .post("/create-game/Iniesta")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Arrays.toString(new Byte[0])))
                .andExpect(status().isCreated())
                .andDo(print())
                .andReturn();

        GameSession gameSession = mapper.readValue(storyResult.getResponse().getContentAsString(), GameSession.class);

        assertEquals("Players not equal", "Iniesta", gameSession.getFirstPlayer().getPlayerName());
    }


    @Test
    @Sql("/sql/insertGameSessionOver.sql")
    public void getResult_withSuccess() throws Exception {

        MvcResult storyResult = mockMvc.perform(MockMvcRequestBuilders
                .get("/result/1645554010202"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        GameSession gameSession = mapper.readValue(storyResult.getResponse().getContentAsString(), GameSession.class);

        assertEquals("Sessions not equal", Long.valueOf("1645554010202"), Long.valueOf(gameSession.getSessionCode()));
    }

    @Test
    public void getResult_NotFound() throws Exception {

        MvcResult storyResult = mockMvc.perform(MockMvcRequestBuilders
                .get("/result/1645554010202"))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andReturn();


        ClientError error = mapper.readValue(storyResult.getResponse().getContentAsString(), ClientError.class);

        assertEquals("Player is present", 404, error.getStatus());
    }


}