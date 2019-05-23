package org.aprestos.labs.spring.microservices.model.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Data
@Entity
public class Message implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private String ident;

  @Column(nullable = false)
  private Long timestamp;

  @Column(nullable = false)
  private String text;

}
