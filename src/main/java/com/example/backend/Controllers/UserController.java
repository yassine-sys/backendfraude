package com.example.backend.Controllers;

import com.example.backend.dao.UserRepository;
import com.example.backend.entities.User;
import com.example.backend.entities.UserUpdateRequest;
import com.example.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService ;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }
    @RequestMapping(method= RequestMethod.GET)
    public List<User> getList() {

        return userService.getListUser();
    }

   // @RequestMapping(value="/add",method=RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
    /*@PostMapping( "/add")
    public void addUtilisateur(@RequestBody User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setEtat("A");
        //user.setAdmin(user.isAdmin());
        //System.out.println(user.getRole().getRole().toString());
        userService.addUser(user);
    }
    @RequestMapping(value="/edit",method=RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
    public void editUtilisateur(@RequestBody User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        //user.setAdmin(user.isAdmin());
        userService.editUser(user);
    }*/

    @RequestMapping(value="/add/{id}",method=RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addUtilisateur(@RequestBody User user,@PathVariable Long id) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        //user.setAdmin(user.isAdmin());
        //System.out.println(user.getRole().getRole().toString());
        userService.addUser(user,id);
    }
    @RequestMapping(value="/edit/{idGrp}", method=RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void editUtilisateur(@RequestBody User user, @PathVariable Long idGrp) {
        System.out.println("===============Controller");

        user.setUser_group(null);

        // Check if password is not null and not empty
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            System.out.println("===============Controller0");
        } else {
            // If password is null or empty, retain the old password or handle accordingly
            // For now, assuming you want to keep the old password
            User existingUser = userService.findById(user.getuId());
            user.setPassword(existingUser.getPassword());
            System.out.println("===============Controller1");
        }

        System.out.println("===============Controller Finale");
        // user.setAdmin(user.isAdmin());
        userService.editUser(user, idGrp);
    }
    @RequestMapping(value="/delete/{id}",method=RequestMethod.DELETE)
    public void deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
    }
    @RequestMapping(value="/user/{id}",method=RequestMethod.GET)
    public User findbyId(@PathVariable Long id) {
        System.out.println("find by id d5al lel fonction c bon ");
        return userService.findById(id);
    }




//    @PutMapping("/{id}/password/{previousPassword}/{newPassword}")
//    public ResponseEntity<String> changePassword(@PathVariable Long id,
//                                                 @RequestParam("previousPassword") String previousPassword,
//                                                 @RequestParam("newPassword") String newPassword) {
//        Optional<User> user = userRepository.findById(id);
//
//        // Verify the previous password
//        if (!user.get().getPassword().equals(previousPassword)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect previous password");
//        }
//
//        // Update the password
//        user.get().setPassword(newPassword);
//        userRepository.save(user.get());
//        return ResponseEntity.ok("Password changed successfully");
//    }
//
//    @PutMapping("/{id}")
//    public User updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
//        Optional<User> user = userRepository.findById(id);
//        user.get().setUsername(updatedUser.getUsername());
//        user.get().setuMail(updatedUser.getuMail());
//        user.get().setNomUtilisateur(updatedUser.getuMail());
//        user.get().setDateModif(new Date());
//        // Update other fields as needed
//        return userRepository.save(user.get());
//    }


    @PutMapping("/update/{userId}")
    public ResponseEntity<String> updateUser(@PathVariable Long userId, @RequestBody UserUpdateRequest request) {
        // Retrieve the user from the database
        User user = userService.findById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // Check if the old password matches the one in the database
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect old password");
        }

        // Update the fields that can be changed
        if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            user.setUsername(request.getUsername());
        }
        if (request.getNomUtilisateur() != null && !request.getNomUtilisateur().isEmpty()) {
            user.setNomUtilisateur(request.getNomUtilisateur());
        }
        if (request.getUMail() != null && !request.getUMail().isEmpty()) {
            user.setuMail(request.getUMail());
        }

        // Check if the user wants to change the password
        if (request.getNewPassword() != null && !request.getNewPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(request.getNewPassword());
            user.setPassword(encodedPassword);
        }

        // Save the updated user information
        userRepository.save(user);

        return ResponseEntity.ok().body("{\"message\": \"User updated successfully\"}");
    }

    @PutMapping("/{username}/password")
    public ResponseEntity<?> updatePassword(
            @PathVariable("username") String username,
            @RequestParam(value = "oldPassword") String oldPassword,
            @RequestParam(value = "newPassword", required = false) String newPassword) {
        try {
            User updatedUser = userService.updatePassword(username, oldPassword, newPassword);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @RequestMapping(value="/email/{email}",method=RequestMethod.GET)
    public User findbyEmail(@PathVariable String email) {
        Optional<User> user =userService.findByEmail(email);
        return user.get();
    }

    @RequestMapping(value="/username/{username}",method=RequestMethod.GET)
    public User findbyUsername(@PathVariable String username) {
        Optional<User> user = userService.findByUsername(username);
        return user.get();
    }

}

