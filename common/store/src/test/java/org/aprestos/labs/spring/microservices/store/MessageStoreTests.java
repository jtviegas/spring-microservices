package org.aprestos.labs.spring.microservices.store;

import org.aprestos.labs.spring.microservices.model.dto.MessageDto;
import org.aprestos.labs.spring.microservices.store.services.MessageStore;
import org.aprestos.labs.spring.microservices.testtools.UtilsModel;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.Optional;


@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ContextConfiguration
public class MessageStoreTests {

  @Autowired
  private MessageStore store;

  @Transactional
  @Test
  public void test_all() {

    Assert.assertEquals(0, store.getMessages().size());
    MessageDto dto = UtilsModel.createMessage();
    String ident = store.postMessage(dto);
    Optional<MessageDto> saved = store.getMessage(ident);
    Assert.assertTrue(saved.isPresent());
    Assert.assertEquals(dto, saved.get());
    Assert.assertEquals(1, store.getMessages().size());
    store.delMessage(dto.getIdent());
    Assert.assertEquals(0, store.getMessages().size());

  }


}
