package org.aprestos.labs.spring.microservices.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class Problem implements Serializable {

    private Long id;
    @NotNull
    private Integer capacity;
    @NotNull
    private Item[] items;

    public Problem(){}

    public Problem(Long id, Integer capacity, Item[] items){
        this.id = id;
        this.capacity = capacity;
        this.items = items;
    }

    public Problem(Integer capacity, Item[] items){
        this.capacity = capacity;
        this.items = items;
    }

}
