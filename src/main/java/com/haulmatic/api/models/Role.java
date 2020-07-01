package com.haulmatic.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "role")
public class Role {
    public enum RoleType {
        @JsonProperty("Driver")
        DRIVER,
        @JsonProperty("Assistant")
        ASSISTANT
    }

    private String firstName;
    private String lastName;
    @Id
    private String nic;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getNic() {
        return nic;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }
}
