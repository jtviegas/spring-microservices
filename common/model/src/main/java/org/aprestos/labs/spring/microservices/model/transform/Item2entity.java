package org.aprestos.labs.spring.microservices.model.transform;

import org.aprestos.labs.spring.microservices.model.entities.Item;

import java.util.function.Function;

public class Item2entity implements Function<org.aprestos.labs.spring.microservices.model.dto.Item, Item> {


    @Override
    public Item apply(org.aprestos.labs.spring.microservices.model.dto.Item item) {
        Item result = new Item();

        result.setIdent(item.getId());
        result.setValue(item.getValue());
        result.setWeight(item.getWeight());

        return result;
    }
}
