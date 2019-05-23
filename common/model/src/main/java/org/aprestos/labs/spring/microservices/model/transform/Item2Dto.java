package org.aprestos.labs.spring.microservices.model.transform;

import org.aprestos.labs.spring.microservices.model.entities.Item;

import java.util.function.Function;

public class Item2Dto implements Function<Item, org.aprestos.labs.spring.microservices.model.dto.Item> {


    @Override
    public org.aprestos.labs.spring.microservices.model.dto.Item apply(Item item) {
        return new org.aprestos.labs.spring.microservices.model.dto.Item(item.getIdent() , item.getValue(), item.getWeight());
    }
}
