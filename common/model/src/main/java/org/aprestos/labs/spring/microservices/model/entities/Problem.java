package org.aprestos.labs.spring.microservices.model.entities;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
public class Problem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ident;
    @Column(nullable = false)
    private Integer capacity;
    @OneToMany(/*mappedBy = "problem", */cascade = CascadeType.ALL)
    private List<Item> items;

}
