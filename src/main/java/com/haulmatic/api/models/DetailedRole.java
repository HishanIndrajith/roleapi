package com.haulmatic.api.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "role")
public class DetailedRole extends Role {

    private String organization;
    private RoleType roleType;

    public String getOrganization() {
        return organization;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public void setOrganization(String organization) {
        this.organization = organization;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }
}
