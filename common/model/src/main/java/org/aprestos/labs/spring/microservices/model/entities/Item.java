package org.aprestos.labs.spring.microservices.model.entities;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
public class Item implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ident;
    @Column(nullable = false)
    private Integer value;
    @Column(nullable = false)
    private Integer weight;
/*    @ManyToOne
    @JoinColumn(name = "problem_id")
    private Problem problem;*/

}
