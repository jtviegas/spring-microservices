package org.aprestos.labs.spring.microservices.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aprestos.labs.spring.microservices.api.exceptions.ExceptionResponse;
import org.aprestos.labs.spring.microservices.model.dto.MessageDto;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;




@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class MessagesTests {

  @Autowired
   private MockMvc mockMvc;
  
  @Autowired
  private ObjectMapper jsonMapper;
  
    @Test
    public void messageStateRoundTrip() throws Exception {

        MessageDto messageDto = new MessageDto();
        messageDto.setText("aaa");
        
        
        MvcResult result = this.mockMvc.perform(post("/api/messages").accept("application/json")
                .contentType("application/json")
            .content(jsonMapper.writeValueAsString(messageDto)))
          .andDo(print()).andExpect(status().isOk()).andReturn();

        MessageDto outMsg = jsonMapper.readValue(result.getResponse().getContentAsString(), MessageDto.class);
        Assert.assertTrue(0 < Integer.parseInt(outMsg.getIdent()) );
        Assert.assertTrue(System.currentTimeMillis() > outMsg.getTimestamp() );

    }

    @Test
    public void messageValidationControllerExceptionHandler() throws Exception {

        MessageDto messageDto = new MessageDto();
        messageDto.setText(null);
        MvcResult result =  this.mockMvc.perform(post("/api/messages").accept("application/json")
                .contentType("application/json")
                .content(jsonMapper.writeValueAsString(messageDto)))
                .andDo(print()).andExpect(status().is4xxClientError()).andReturn();

        MessageDto response = jsonMapper.readValue(result.getResponse().getContentAsString(), MessageDto.class);
        Assert.assertEquals("text: must not be null", response.getText());
    }

    @Test
    public void messageValidationServerException() throws Exception {

        MessageDto messageDto = new MessageDto();
        messageDto.setText("error");
        MvcResult result =  this.mockMvc.perform(post("/api/messages").accept("application/json")
                .contentType("application/json")
                .content(jsonMapper.writeValueAsString(messageDto)))
                .andDo(print()).andExpect(status().isInternalServerError()).andReturn();
        ExceptionResponse response = jsonMapper.readValue(result.getResponse().getContentAsString(), ExceptionResponse.class);
        Assert.assertTrue(response.getMsg().startsWith("something was not correct"));
    }

}
