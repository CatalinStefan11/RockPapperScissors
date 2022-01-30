package com.rockpaperscissors.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rockpaperscissors.exception.model.ClientError;
import com.rockpaperscissors.model.actors.Player;
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
import static org.springframework.test.util.AssertionErrors.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class PlayerControllerTest {

    private static final ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    static void init(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        mapper.registerModule(new JavaTimeModule());
        mapper.setDateFormat(df);

    }


    @Autowired
    private MockMvc mockMvc;

    @Test
    public void createPlayer_withSuccess() throws Exception {

        MvcResult storyResult = mockMvc.perform(MockMvcRequestBuilders
                .post("/player/Catalin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Arrays.toString(new Byte[0])))
                .andExpect(status().isCreated())
                .andDo(print())
                .andReturn();

        Player player = mapper.readValue(storyResult.getResponse().getContentAsString(), Player.class);

        assertEquals("Players not equal" , "Catalin", player.getPlayerName());
    }

    @Test
    @Sql("/sql/insertPlayer.sql")
    public void createPlayer_alreadyExists() throws Exception {

        MvcResult storyResult = mockMvc.perform(MockMvcRequestBuilders
                .post("/player/Ronaldo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Arrays.toString(new Byte[0])))
                .andExpect(status().isBadRequest())
                .andReturn();

        ClientError error = mapper.readValue(storyResult.getResponse().getContentAsString(), ClientError.class);

        assertEquals("Player already exists" , 400, error.getStatus());
    }

    @Test
    @Sql("/sql/insertPlayer.sql")
    public void readyPlayer() throws Exception {

        MvcResult storyResult = mockMvc.perform(MockMvcRequestBuilders
                .put("/ready-player/Ronaldo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Arrays.toString(new Byte[0])))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        Player player = mapper.readValue(storyResult.getResponse().getContentAsString(), Player.class);

        assertEquals("Player states not equal" , Player.PlayerState.READY, player.getCurrentState());
    }


    @Test
    @Sql("/sql/insertPlayer.sql")
    public void deletePlayer() throws Exception {

        MvcResult storyResult = mockMvc.perform(MockMvcRequestBuilders
                .delete("/player/Ronaldo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Arrays.toString(new Byte[0])))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andReturn();

    }

    @Test
    public void deletePlayer_NotFound() throws Exception {

        MvcResult storyResult = mockMvc.perform(MockMvcRequestBuilders
                .delete("/player/Ronaldo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Arrays.toString(new Byte[0])))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andReturn();

        ClientError error = mapper.readValue(storyResult.getResponse().getContentAsString(), ClientError.class);

        assertEquals("Player is present" , 404, error.getStatus());

    }

    @Test
    @Sql("/sql/insertPlayer.sql")
    public void getPlayer() throws Exception {

        MvcResult storyResult = mockMvc.perform(MockMvcRequestBuilders
                .get("/player/Ronaldo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Arrays.toString(new Byte[0])))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        Player player = mapper.readValue(storyResult.getResponse().getContentAsString(), Player.class);

        assertNotNull("Player is null" , player);

    }

    @Test
    public void getPlayer_notFound() throws Exception {

        MvcResult storyResult = mockMvc.perform(MockMvcRequestBuilders
                .get("/player/Ronaldo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Arrays.toString(new Byte[0])))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andReturn();

        ClientError error = mapper.readValue(storyResult.getResponse().getContentAsString(), ClientError.class);

        assertEquals("Player is present" , 404, error.getStatus());

    }


}
