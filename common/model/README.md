# data-model
java data access model
package org.aprestos.labs.apis.springboot2.testutils;

import org.apache.commons.lang3.RandomStringUtils;
import org.aprestos.labs.apis.springboot2.apiutils.dto.MessageDto;

import java.util.Date;

public class UtilsMessage {

    public static MessageDto create(){
        MessageDto result = new MessageDto();
        result.setIdent(RandomStringUtils.random(12, true, true));
        result.setTimestamp(new Date().getTime());
        result.setText(RandomStringUtils.random(32, true, false));
        return result;
    }

}