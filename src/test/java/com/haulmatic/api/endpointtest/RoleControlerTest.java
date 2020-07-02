package com.haulmatic.api.endpointtest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haulmatic.api.models.DetailedRole;
import com.haulmatic.api.models.Role;
import com.haulmatic.api.repositories.RoleRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@RunWith(SpringRunner.class)
@SpringBootTest
public class RoleControlerTest {

    //to Generate JSON content from Java objects
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    @Autowired
    private RoleRepository roleRepository;
    //Test RestTemplate to invoke the APIs.
    private RestTemplate restTemplate = new RestTemplate();
    private String nic = "952080660v";
    private String firstName = "Hishan";
    private String lastName = "Indrajith";
    private String organization = "organizationABC";

    @Test
    public void testReadRoleEndPoint() {
        //Create a new role using the RoleRepository API
        DetailedRole role = new DetailedRole();
        role.setNic(nic);
        role.setFirstName(firstName);
        role.setLastName(lastName);
        role.setOrganization(organization);
        role.setRoleType(Role.RoleType.ASSISTANT);
        roleRepository.save(role);

        String nic = role.getNic();

        //Now make a call to the API to get details of the role
        DetailedRole apiResponse = restTemplate.getForObject("http://localhost:8080/api/roles/" + nic, DetailedRole.class);

        //Verify that the data from the API and data saved in the DB are same
        assertNotNull(apiResponse);
        assertEquals(role.getOrganization(), apiResponse.getOrganization());
        assertEquals(role.getFirstName(), apiResponse.getFirstName());
        assertEquals(role.getLastName(), apiResponse.getLastName());
        assertEquals(role.getNic(), apiResponse.getNic());
        assertEquals(role.getRoleType(), apiResponse.getRoleType());

        //Delete the Test data created
        roleRepository.delete(role);
    }

    @Test
    public void testCreateRoleEndPoint() throws JsonProcessingException {
        String roleType = "Driver";
        // Find and Delete any entry with testing nic
        Optional<DetailedRole> roleData = roleRepository.findById(nic);
        if (roleData.isPresent()) {
            roleRepository.deleteById(nic);
        }
        //Building the Request body data
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("firstName", firstName);
        requestBody.put("lastName", lastName);
        requestBody.put("nic", nic);
        requestBody.put("organization", organization);
        requestBody.put("roleType", roleType);

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        //Creating http entity object with request body and headers
        HttpEntity<String> httpEntity =
                new HttpEntity<>(OBJECT_MAPPER.writeValueAsString(requestBody), requestHeaders);

        //Invoking the API
        Map apiResponse =
                restTemplate.postForObject("http://localhost:8080/api/roles", httpEntity, Map.class);

        assertNotNull(apiResponse);

        //Fetching the Role details directly from the DB to verify the API succeeded
        Optional<DetailedRole> roleFromDb = roleRepository.findById(nic);
        if (roleFromDb.isPresent()) {
            DetailedRole detailedRoleFromDb = roleFromDb.get();
            assertRoleFromDbwithRequestObject(detailedRoleFromDb);
            roleRepository.delete(detailedRoleFromDb);
        }
    }

    @Test
    public void testUpdateRoleEndPoint() throws JsonProcessingException {
        String organizationOld = "organizationOld";
        String roleTypeNew = "Driver";
        // Find and Delete any entry with testing nic
        roleRepository.findById(nic);
        Optional<DetailedRole> roleData = roleRepository.findById(nic);
        if (roleData.isPresent()) {
            // Delete from database if availble
            roleRepository.deleteById(nic);

        }
        //Create a new role directly in DB
        DetailedRole role = new DetailedRole();
        role.setNic(nic);
        role.setFirstName(firstName);
        role.setLastName(lastName);
        role.setOrganization(organizationOld);
        role.setRoleType(Role.RoleType.ASSISTANT);
        roleRepository.save(role);

        //Building the Request body data with update data
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("firstName", firstName);
        requestBody.put("lastName", lastName);
        requestBody.put("organization", organization);
        requestBody.put("roleType", roleTypeNew);

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        //Creating http entity object with request body and headers
        HttpEntity<String> httpEntity =
                new HttpEntity<>(OBJECT_MAPPER.writeValueAsString(requestBody), requestHeaders);

        //Invoking the API to update
        Map apiResponse = restTemplate.exchange("http://localhost:8080/api/roles/" + nic,
                HttpMethod.PUT, httpEntity, Map.class).getBody();
        assertNotNull(apiResponse);

        //Fetching the Role details directly from the DB to verify the API succeeded
        Optional<DetailedRole> roleFromDb = roleRepository.findById(nic);
        if (roleFromDb.isPresent()) {
            DetailedRole detailedRoleFromDb = roleFromDb.get();
            assertRoleFromDbwithRequestObject(detailedRoleFromDb);
            //Delete the data added for testing
            roleRepository.delete(detailedRoleFromDb);
        }
    }

    @Test
    public void testDeleteRoleEndPoint() {
        //Create a new role using the RoleRepository API
        DetailedRole role = new DetailedRole();
        role.setNic(nic);
        role.setFirstName(firstName);
        role.setLastName(lastName);
        role.setOrganization(organization);
        role.setRoleType(Role.RoleType.ASSISTANT);
        roleRepository.save(role);

        //Now Invoke the API to delete the role
        restTemplate.delete("http://localhost:8080/api/roles/" + nic);

        //Try to fetch from the DB directly
        Optional<DetailedRole> roleFromDb = roleRepository.findById(nic);
        //and assert that there is no data found
        roleFromDb.ifPresent(Assert::assertNull);
    }

    @Test
    public void testGetListOfRolesEndPoint() {
        //Create 3 new role using the RoleRepository API, first two are similar in organization and role type.
        //Third is different in role type
        DetailedRole role1 = new DetailedRole();
        role1.setNic(nic);
        role1.setFirstName(firstName);
        role1.setLastName(lastName);
        role1.setOrganization(organization);
        role1.setRoleType(Role.RoleType.ASSISTANT);
        roleRepository.save(role1);

        DetailedRole role2 = new DetailedRole();
        role2.setNic("945187552v");
        role2.setFirstName("Nicholas");
        role2.setLastName("Lyman");
        role2.setOrganization(organization);
        role2.setRoleType(Role.RoleType.ASSISTANT);
        roleRepository.save(role2);

        DetailedRole role3 = new DetailedRole();
        role3.setNic("945178962v");
        role3.setFirstName("Piers");
        role3.setLastName("Hughes");
        role3.setOrganization("organizationXYZ");
        role3.setRoleType(Role.RoleType.ASSISTANT);
        roleRepository.save(role3);


        //Now make a call to the API to get details of the role
        String url = "http://localhost:8080/api/roles?organization=" +
                organization +
                "&role_type=" +
                "Assistant";
        List apiResponse = restTemplate.getForObject(url, List.class);
        //Assert the response from the API

        assertEquals(2, apiResponse.size());
        //Delete the test data created
        roleRepository.delete(role1);
        roleRepository.delete(role2);
        roleRepository.delete(role3);
    }

    private void assertRoleFromDbwithRequestObject(DetailedRole detailedRoleFromDb) {
        assertEquals(firstName, detailedRoleFromDb.getFirstName());
        assertEquals(lastName, detailedRoleFromDb.getLastName());
        assertEquals(nic, detailedRoleFromDb.getNic());
        assertEquals(Role.RoleType.DRIVER, detailedRoleFromDb.getRoleType());
        assertEquals(organization, detailedRoleFromDb.getOrganization());
    }
}

