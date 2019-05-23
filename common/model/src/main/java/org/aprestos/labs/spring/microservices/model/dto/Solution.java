package org.aprestos.labs.spring.microservices.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.Serializable;

@Data
public class Solution implements Serializable {

    private Long id;
    @NotNull
    private Item[] items;
    @Positive
    private Long time;
    @Positive
    private Integer value;

    public Solution(){}

    public Solution(Long id, Item[] items, Long time, Integer value){
        this.id = id;
        this.items = items;
        this.time = time;
        this.value = value;
    }

    public Solution(Item[] items, Long time, Integer value){
        this.items = items;
        this.time = time;
        this.value = value;
    }

}
