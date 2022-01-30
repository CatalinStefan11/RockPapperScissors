package com.rockpaperscissors.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rockpaperscissors.model.actors.Player;
import com.rockpaperscissors.model.dto.PlayRequest;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class GameControllerTest {

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
    @Sql("/sql/insertGameSessionAccepted.sql")
    public void play_withSuccess() throws Exception {

        MvcResult storyResult = mockMvc.perform(MockMvcRequestBuilders
                .post("/play")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "playerName":"Sofi",
                            "sessionCode":"1645554010202",
                            "move":"paper"
                        }
                        """))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

    }


    @Test
    @Sql("/sql/insertGameSessionAccepted.sql")
    public void play_playerNotFound() throws Exception {

        MvcResult storyResult = mockMvc.perform(MockMvcRequestBuilders
                .post("/play")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "playerName":"Ozil",
                            "sessionCode":"1645554010202",
                            "move":"paper"
                        }
                        """))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andReturn();

    }
}