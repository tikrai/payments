package com.gmail.tikrai.payments.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gmail.tikrai.payments.util.Generated;
import java.util.Objects;

public class IpApiResponse {

  private final String country;

  @JsonCreator
  public IpApiResponse(@JsonProperty("country") String country) {
    this.country = country;
  }

  @JsonProperty("country")
  public String country() {
    return country;
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
    IpApiResponse that = (IpApiResponse) o;
    return Objects.equals(country, that.country);
  }

  @Override
  @Generated
  public int hashCode() {
    return Objects.hash(country);
  }
}
