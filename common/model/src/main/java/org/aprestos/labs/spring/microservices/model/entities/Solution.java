package org.aprestos.labs.spring.microservices.model.entities;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
public class Solution implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ident;
    @OneToMany(/*mappedBy = "solution", */cascade = CascadeType.ALL)
    private List<Item> items;
    @Column(nullable = false)
    private Long time;
    @Column(nullable = false)
    private Integer value;

}
