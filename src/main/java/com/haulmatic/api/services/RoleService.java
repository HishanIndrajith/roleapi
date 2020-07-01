package com.haulmatic.api.services;

import com.haulmatic.api.models.Role;
import com.haulmatic.api.models.Role.RoleType;
import com.haulmatic.api.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    private RoleRepository roleRepository;

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public ResponseEntity<Role> getRoleByNic(String nic) {
        Optional<Role> roleData = roleRepository.findById(nic);
        return roleData.map(role -> new ResponseEntity<>(role, HttpStatus.OK)).orElseGet(() ->
                new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    public ResponseEntity<Role> createRole(Role role) {
        Optional<Role> roleData = roleRepository.findById(role.getNic());
        if (! roleData.isPresent()) {
            try {
                Role roleSaved = roleRepository.save(role);
                return new ResponseEntity<>(roleSaved, HttpStatus.CREATED);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
            }
        }else{
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }


    public ResponseEntity<Role> updateRole(String nic, Role role) {
        Optional<Role> roleData = roleRepository.findById(nic);
        if (roleData.isPresent()) {
            Role roleFromDb = roleData.get();
            roleFromDb.setOrganization(role.getOrganization());
            roleFromDb.setFirstName(role.getFirstName());
            roleFromDb.setLastName(role.getLastName());
            roleFromDb.setRoleType(role.getRoleType());
            return new ResponseEntity<>(roleRepository.save(roleFromDb), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<HttpStatus> deleteRole(String nic) {
        try {
            roleRepository.deleteById(nic);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/roles")
    public ResponseEntity<List<Role>> findByOrganization(String organization, String roleTypeStr) {
        RoleType roleType;
        switch (roleTypeStr) {
            case "Assistant":
                roleType = RoleType.ASSISTANT;
                break;
            case "Driver":
                roleType = RoleType.DRIVER;
                break;
            default:
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            List<Role> roles = roleRepository.findByOrganizationAndRoleType(organization, roleType);
            roles.forEach(role -> {role.setOrganization(null);role.setRoleType(null);});

            if(roles.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(roles, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
