package com.gmail.tikrai.payments.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gmail.tikrai.payments.util.Generated;
import java.util.Objects;

public class IdResponse {

  private final int id;

  @JsonCreator
  public IdResponse(@JsonProperty("id") int id) {
    this.id = id;
  }

  @JsonProperty("id")
  public int id() {
    return id;
  }

  @Override
  @Generated
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    IdResponse that = (IdResponse) o;
    return id == that.id;
  }

  @Override
  @Generated
  public int hashCode() {
    return Objects.hash(id);
  }
}
