package org.aprestos.labs.spring.microservices.testtools;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.aprestos.labs.spring.microservices.model.dto.MessageDto;
import org.aprestos.labs.spring.microservices.model.dto.Problem;
import org.aprestos.labs.spring.microservices.model.dto.Task;
import org.aprestos.labs.spring.microservices.model.dto.Item;

import java.util.Date;

public class UtilsModel {

    public static MessageDto createMessage(){
        MessageDto result = new MessageDto();
        result.setIdent(RandomStringUtils.random(12, true, true));
        result.setTimestamp(new Date().getTime());
        result.setText(RandomStringUtils.random(32, true, false));
        return result;
    }

    public static Task createTask(){

        Task result = new Task(createProblem());
        return result;
    }

    public static Problem createProblem(){

        Problem result = new Problem(RandomUtils.nextInt(24, 36),
                new Item[]{
                        new Item( RandomUtils.nextInt(12, 36), RandomUtils.nextInt(12, 36))
                        , new Item( RandomUtils.nextInt(12, 36), RandomUtils.nextInt(12, 36))
                        , new Item( RandomUtils.nextInt(12, 36), RandomUtils.nextInt(12, 36))
                });

        return result;
    }

}
