package com.example.FlashCards.controllers;

import com.example.FlashCards.DTOs.UserDTO;
import com.example.FlashCards.configurations.Entity2DTOMapper;
import com.example.FlashCards.entities.User;
import com.example.FlashCards.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @MockBean
    UserService userService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    Entity2DTOMapper modelMapper = new Entity2DTOMapper();

    User u1;
    User u2;

    String baseURL = "/users";

    @Test
    @DisplayName("GET /users calls UserService.getAllUsers() to return all UserDTOs without password")
    public void getAllUsers_success() throws Exception {
        u1 = User.builder()
                .id(1L)//mock the process, so set the id here.
                .name("u1")
                .build();
        u2 = User.builder()
                .id(2L)
                .name("u2")
                .build();
        List<User> users = List.of(u1, u2);
        // the returned JSON of user DTO list should not contain password
        List<UserDTO> userDTOs = users.stream()
                .map(user -> modelMapper.map(user, UserDTO.class)).collect(Collectors.toList());

        when(userService.getAllUsers())
                .thenReturn(userDTOs);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(baseURL)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(users.size())))
                .andExpect(jsonPath("$[0].id", is(u1.getId()), Long.class))
                .andExpect(jsonPath("$[1].id", is(u2.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(u1.getName())))
//                .andExpect(jsonPath("$[*].id", containsInAnyOrder(u1.getId().intValue(), u2.getId().intValue())))//don't know how to set the target type to Iterable<Long>
                .andExpect(jsonPath("$[*].name", containsInAnyOrder(u1.getName(), u2.getName())))
                .andExpect(jsonPath("$[*]", not(hasKey("password"))));
        verify(userService).getAllUsers();
    }

    @Test
    @DisplayName("GET /users/{id} calls UserService.getUserById(id) to return the correct UserDTO without password")
    public void getUserById_success() throws Exception {
        u1 = User.builder()
                .id(1L)
                .name("u1")
                .build();

        when(userService.getUserById(u1.getId()))
                .thenReturn(modelMapper.map(u1, UserDTO.class));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(baseURL + "/" + u1.getId())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is(u1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(u1.getName())))
                .andExpect(jsonPath("$", not(hasKey("password"))));
        verify(userService).getUserById(u1.getId());
    }

    @Test
    @DisplayName("POST /users with userDTO calls UserService.add(userDTO) to return the saved UserDTO without password")
    public void postUser_success() throws Exception {
        u1 = User.builder()
                .id(1L)
                .name("u1")
                .build();

        UserDTO u1DTO = modelMapper.map(u1, UserDTO.class);
        when(userService.add(u1DTO))
                .thenReturn(u1DTO);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(baseURL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(u1DTO))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is(u1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(u1.getName())))
                .andExpect(jsonPath("$", not(hasKey("password"))));
        verify(userService).add(u1DTO);
    }

    @Test
    @DisplayName("PUT /users with userDTO calls UserService.udpateUser(userDTO) to return the updated UserDTO without password")
    public void putUser_success() throws Exception {
        u1 = User.builder()
                .id(1L)
                .name("u1")
                .build();

        u2 = User.builder()
                .id(2L)
                .name("u2")
                .build();

        User updatedU1 = User.builder()
                .id(1L)
                .name(u2.getName())
                .build();

        UserDTO u2DTO = modelMapper.map(u2, UserDTO.class);
        UserDTO updatedU1DTO = modelMapper.map(updatedU1, UserDTO.class);
        when(userService.updateUser(u2DTO, u1.getId()))
                .thenReturn(updatedU1DTO);

        mockMvc.perform(MockMvcRequestBuilders
                        .put(baseURL + "/" + u1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(u2DTO))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is(u1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(u2.getName())))
                .andExpect(jsonPath("$", not(hasKey("password"))));
        verify(userService).updateUser(u2DTO, u1.getId());
    }

    @Test
    @DisplayName("DELETE /users with userDTO calls UserService.deleteById(userDTO) to return the updated UserDTO without password")
    public void deleteUserById_success() throws Exception {
        u1 = User.builder()
                .id(1L)
                .name("u1")
                .build();

        UserDTO u1DTO = modelMapper.map(u1, UserDTO.class);
        when(userService.getAllUsers())
                .thenReturn(List.of(u1DTO))
                .thenReturn(List.of());

        mockMvc.perform(MockMvcRequestBuilders
                        .get(baseURL)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete(baseURL + "/" + u1.getId())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is("Deleted use with id " + u1.getId())));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(baseURL)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(userService).deleteById(u1.getId());
        verify(userService, times(2)).getAllUsers();
    }

    @Test
    @DisplayName("Register success")
    public void register_success() throws Exception {
        u1 = User.builder()
                .name("u1")
                .password("p1")
                .build();
        UserDTO u1DTO = modelMapper.map(u1, UserDTO.class);
        User u1WithId = User.builder()
                .id(1L)
                .name(u1.getName())
                .password(u1.getPassword())
                .build();
        UserDTO u1IdDTO = modelMapper.map(u1WithId, UserDTO.class);

        when(userService.add(u1DTO))
                .thenReturn(u1IdDTO);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(baseURL + "/register")
                        .queryParam("username", u1DTO.getName())
                        .queryParam("password", u1DTO.getPassword())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is(u1WithId.getId()), Long.class));
    }

    @Test
    @DisplayName("login success")
    public void login_success() throws Exception {
        u1 = User.builder()
                .name("u1")
                .password("p1")
                .build();
        UserDTO u1DTO = modelMapper.map(u1, UserDTO.class);

        when(userService.login(u1DTO))
                .thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(baseURL + "/login")
                        .queryParam("username", u1DTO.getName())
                        .queryParam("password", u1DTO.getPassword())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", equalTo("Hello, " + u1DTO.getName())));
        verify(userService).login(u1DTO);
    }

    @Test
    @DisplayName("login failure")
    public void login_failure() throws Exception {
        u1 = User.builder()
                .name("u1")
                .password("p1")
                .build();
        UserDTO u1DTO = modelMapper.map(u1, UserDTO.class);

        when(userService.login(u1DTO))
                .thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(baseURL + "/login")
                        .queryParam("username", u1DTO.getName())
                        .queryParam("password", u1DTO.getPassword())
                        .accept(MediaType.TEXT_PLAIN)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$", equalTo("Incorrect username or password")));
        verify(userService).login(u1DTO);
    }
}
