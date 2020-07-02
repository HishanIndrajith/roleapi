package com.haulmatic.api.controllers;

import com.haulmatic.api.models.DetailedRole;
import com.haulmatic.api.models.Role;
import com.haulmatic.api.services.RoleService;
import io.swagger.annotations.ApiOperation;
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
    @GetMapping("/roles/{nic}")
    public ResponseEntity<DetailedRole> getRole(@PathVariable("nic") String nic) {
        return roleService.getRoleByNic(nic);
    }

    @ApiOperation(value = "Create a new role", response = DetailedRole.class)
    @PostMapping("/roles")
    public ResponseEntity<DetailedRole> createRole(@RequestBody DetailedRole detailedRole) {
        return roleService.createRole(detailedRole);
    }

    @ApiOperation(value = "Update a role", response = DetailedRole.class)
    @PutMapping("/roles/{nic}")
    public ResponseEntity<DetailedRole> updateRole(@PathVariable("nic") String nic, @RequestBody DetailedRole detailedRole) {
        return roleService.updateRole(nic, detailedRole);
    }

    @ApiOperation(value = "Delete a role")
    @DeleteMapping("/roles/{nic}")
    public ResponseEntity<HttpStatus> deleteRole(@PathVariable("nic") String nic) {
        return roleService.deleteRole(nic);
    }

    @ApiOperation(value = "View a list of available roles by the organization and the role type", response = Iterable.class)
    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getRolesByOrganizationAndRoleType(
            @RequestParam(value = "organization") String organization,
            @RequestParam(value = "role_type") String roleType) {
        return roleService.findByOrganization(organization, roleType);
    }
}
