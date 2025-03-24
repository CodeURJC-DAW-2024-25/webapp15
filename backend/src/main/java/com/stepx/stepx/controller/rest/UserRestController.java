package com.stepx.stepx.controller.rest;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.stepx.stepx.dto.*;
import com.stepx.stepx.model.Review;
import com.stepx.stepx.model.Shoe;
import com.stepx.stepx.model.User;
import com.stepx.stepx.repository.*;
import com.stepx.stepx.service.*;
import jakarta.servlet.http.HttpServletRequest;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/user")
public class UserRestController {

    @Autowired
    private ReviewService reviewService;
    @Autowired
    private ShoeRepository shoeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ShoeService shoeService;
    @Autowired
    private UserService userService;

    // get all users
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.findAllUsers();
        if (users == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(users);
    }
    // get a order item by id
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO userDto = userService.findUserById(id);

        if (userDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userDto);
    }

    //create User
    @PostMapping
    public ResponseEntity<UserDTO> createUserAPI(@RequestBody UserDTO userDto) {

        userDto = userService.createUserAPI(userDto);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(userDto.id()).toUri();

		return ResponseEntity.created(location).body(userDto);
    }

    @DeleteMapping("/{id}")
    public UserDTO deleteUser(@PathVariable Long id) {

        return userService.deleteUser(id);
    }

    @PutMapping("/{id}")
    public UserDTO ReplaceUser(@PathVariable Long id,@RequestBody UserDTO updatedUserDTO)throws SQLException {
        return userService.replaceUser(id, updatedUserDTO);
    }

    //--Images with user---
    @GetMapping("/{id}/image")
    public ResponseEntity<Object> getPostImage(@PathVariable long id)
            throws SQLException, IOException {

        Resource userImage = userService.getUserImage(id);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                .body(userImage);
    }

    @PostMapping("/{id}/image")
	public ResponseEntity<Object> createUserImage(@PathVariable long id, @RequestParam MultipartFile imageFile)
			throws IOException {

		URI location = fromCurrentRequest().build().toUri();

		userService.createUserImage(id, location, imageFile.getInputStream(), imageFile.getSize());

		return ResponseEntity.created(location).build();

	}

    @PutMapping("/{id}/image")
	public ResponseEntity<Object> replaceUserImage(@PathVariable long id, @RequestParam MultipartFile imageFile)
			throws IOException {

		userService.replaceUserImage(id, imageFile.getInputStream(), imageFile.getSize());

		return ResponseEntity.noContent().build();
	}

    @DeleteMapping("/{id}/image")
	public ResponseEntity<Object> deleteUserImage(@PathVariable long id)
			throws IOException {

		userService.deleteUserImage(id);

		return ResponseEntity.noContent().build();
	}

}
