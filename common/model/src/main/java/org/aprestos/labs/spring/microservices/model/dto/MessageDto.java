package org.aprestos.labs.spring.microservices.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class MessageDto implements Serializable {

  @JsonIgnore
  private static final long serialVersionUID = 1L;

  @JsonProperty
  private String ident;

  private Long timestamp;

  @JsonProperty
  @NotNull
  private String text;

}
