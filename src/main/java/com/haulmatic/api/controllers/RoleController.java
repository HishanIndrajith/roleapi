package com.haulmatic.api.controllers;

import com.haulmatic.api.models.DetailedRole;
import com.haulmatic.api.models.Role;
import com.haulmatic.api.services.RoleService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/api")
public class RoleController {

    private RoleService roleService;

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    @ApiOperation(value = "Get a role with nic", response = DetailedRole.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved the role"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 400, message = "Bad Request. nic is null")
    })
    @GetMapping("/roles/{nic}")
    public ResponseEntity<DetailedRole> getRole(@PathVariable("nic") String nic) {
        return roleService.getRoleByNic(nic);
    }

    @ApiOperation(value = "Create a new role", response = DetailedRole.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created the role"),
            @ApiResponse(code = 417, message = "Expectation Failed. Couldn't create the role"),
            @ApiResponse(code = 400, message = "Bad Request format"),
            @ApiResponse(code = 409, message = "Conflict. nic already available")
    })
    @PostMapping("/roles")
    public ResponseEntity<DetailedRole> createRole(@RequestBody DetailedRole detailedRole) {
        return roleService.createRole(detailedRole);
    }

    @ApiOperation(value = "Update a role", response = DetailedRole.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated the role"),
            @ApiResponse(code = 404, message = "The resource you were trying to edit is not found"),
            @ApiResponse(code = 400, message = "Bad Request format")
    })
    @PutMapping("/roles/{nic}")
    public ResponseEntity<DetailedRole> updateRole(@PathVariable("nic") String nic, @RequestBody DetailedRole detailedRole) {
        return roleService.updateRole(nic, detailedRole);
    }

    @ApiOperation(value = "Delete a role")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully deleted the role"),
            @ApiResponse(code = 417, message = "Expectation Failed. Couldn't Delete"),
            @ApiResponse(code = 400, message = "Bad Request format. nic is null")
    })
    @DeleteMapping("/roles/{nic}")
    public ResponseEntity<HttpStatus> deleteRole(@PathVariable("nic") String nic) {
        return roleService.deleteRole(nic);
    }

    @ApiOperation(value = "View a list of available roles by the organization and the role type", response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 204, message = "No Content"),
            @ApiResponse(code = 417, message = "Expectation Failed. Couldn't Delete"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getRolesByOrganizationAndRoleType(
            @RequestParam(value = "organization") String organization,
            @RequestParam(value = "role_type") String roleType) {
        return roleService.findByOrganization(organization, roleType);
    }
}
