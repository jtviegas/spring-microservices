package org.aprestos.labs.spring.microservices.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class Item implements Serializable {
    private Long id;
    @NotNull
    private Integer value;
    @NotNull
    private Integer weight;

    public Item(){}

    public Item(Long id, Integer value, Integer weight){
        this.id = id;
        this.value = value;
        this.weight = weight;
    }

    public Item(Integer value, Integer weight){
        this.value = value;
        this.weight = weight;
    }
}
