package org.aprestos.labs.spring.microservices.store;

import org.apache.commons.lang3.RandomUtils;
import org.aprestos.labs.spring.microservices.model.dto.*;
import org.aprestos.labs.spring.microservices.store.exceptions.MissingEntityException;
import org.aprestos.labs.spring.microservices.store.services.TaskStore;
import org.aprestos.labs.spring.microservices.testtools.UtilsModel;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;


@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration
public class TaskStoreTests {

  @Autowired
  private TaskStore store;

  @Transactional
  @Test
  public void test_all() throws MissingEntityException {

    Assert.assertEquals(0, store.get().size());
    Task expected = UtilsModel.createTask();
    Task saved = store.post(expected);

    Assert.assertNotNull(saved.getId());
    expected.setId(saved.getId());
    Assert.assertNotNull(saved.getProblem().getId());
    expected.getProblem().setId(saved.getProblem().getId());
    Assert.assertEquals(expected.getStatuses().size(), saved.getStatuses().size());
    // propagate problem items ids
    for( Item is: saved.getProblem().getItems() ){
      for( Item ie: expected.getProblem().getItems() ){
        if( ie.getValue().equals(is.getValue()) && ie.getWeight().equals(is.getWeight()) ){
          ie.setId(is.getId());
          break;
        }
      }
    }
    Assert.assertEquals(expected, saved);
    expected.getStatuses().add(new TaskState(System.currentTimeMillis(), TaskStatus.submitted));
    saved = store.post(expected);
    Assert.assertEquals(1 , saved.getStatuses().size() );
    expected.getStatuses().get(0).setId(saved.getStatuses().get(0).getId());
    Assert.assertEquals(expected, saved);

    expected.setSolution(new Solution(
            new Item[]{
                    new Item( RandomUtils.nextInt(), RandomUtils.nextInt())
                    , new Item( RandomUtils.nextInt(), RandomUtils.nextInt())
            }, RandomUtils.nextLong(), RandomUtils.nextInt()
    ));

    saved = store.post(expected);
    Assert.assertNotNull(saved.getSolution().getId());
    expected.getSolution().setId(saved.getSolution().getId());
    for(Item is: saved.getSolution().getItems()){
      for(Item ie: expected.getSolution().getItems()){
        if( ie.getValue().equals(is.getValue()) && ie.getWeight().equals(is.getWeight()) ){
          ie.setId(is.getId());
          break;
        }
      }
    }
    Assert.assertEquals(expected, saved);

    store.del(expected.getId());
    Assert.assertEquals(0, store.get().size());

  }


}
