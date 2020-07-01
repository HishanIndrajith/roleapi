package com.haulmatic.api.models;

import com.fasterxml.jackson.annotation.JsonInclude;
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
    private String organization;
    private String firstName;
    private String lastName;
    @Id
    private String nic;
    private RoleType roleType;

    Role(){

    }

    Role(String organization, String firstName, String lastName, String nic, RoleType roleType) {
        this.organization = organization;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nic = nic;
        this.roleType = roleType;
    }

    public String getOrganization() {
        return organization;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getNic() {
        return nic;
    }

    public RoleType getRoleType() {
        return roleType;
    }
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public void setOrganization(String organization) {
        this.organization = organization;
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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }
}
