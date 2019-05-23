package org.aprestos.labs.spring.microservices.model.dto;

import java.io.Serializable;

public enum TaskStatus implements Serializable {
    submitted, started, completed;
}
