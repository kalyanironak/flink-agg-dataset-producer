package com.hack.dataset.entities;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnore;

public interface DataRecord extends Serializable {

    @JsonIgnore
    String getKey();
}
