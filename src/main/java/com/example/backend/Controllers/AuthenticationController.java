package com.example.backend.Controllers;

import com.example.backend.conf.JwtTokenUtil;
import com.example.backend.entities.*;
import com.example.backend.services.ModuleService;
import com.example.backend.services.PasswordResetServiceImpl;
import com.example.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin("*")
@RestController
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private ModuleService moduleService;
    private String tokenResp;
    private Optional<User> userResp;

    @Autowired
    private PasswordResetServiceImpl passwordResetService;

    @RequestMapping(value = "token/generate-token", method = RequestMethod.POST)
    public ResponseEntity<?> register(@RequestBody LoginUser loginUser) throws AuthenticationException {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUser.getUsername(),
                        loginUser.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final Optional<User> user = userService.findByUsername(loginUser.getUsername());

            final String token = jwtTokenUtil.generateToken(user.get());
            System.out.println("token:"+token);
            // Return token in response header
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization",token);
            if(user.get()!=null){
                loginUser.setuId(user.get().getuId());
                loginUser.setuMail(user.get().getuMail());
                customLoginResp response = new customLoginResp();
                if(user.get().getUser_group()!= null){
                    response.setModules(moduleService.findModuleByGroup(user.get().getUser_group().getgId()));
                }else{
                    response.setModules(null);
                }
                response.setUser(loginUser);
                return ResponseEntity.ok().headers(headers).body(response);
            }else{
                return ResponseEntity.ok().headers(headers).build();
            }
    }

    @RequestMapping(value="/check", method = RequestMethod.GET)
    @Transactional
    public ResponseEntity<?> test(@RequestParam(name = "token") String token) {
        if(token!=null){
            final User user = jwtTokenUtil.extractUser(token);
            final String username = jwtTokenUtil.getUsernameFromToken(token);
            final Optional<User> u = userService.findByUsername(username);
            if(username.equals(u.get().getUsername()) && !jwtTokenUtil.isTokenExpired(token)) {
                return ResponseEntity.ok(true);

            }else {
                return ResponseEntity.ok(false);
            }
        }else{
            return ResponseEntity.ok(false);
        }

    }


    @RequestMapping(value="/loginResp", method = RequestMethod.GET)
    @Transactional
    public User getUserData(@RequestParam(name = "token") String token) {
        final Optional<User> user = userService.findByUsername(jwtTokenUtil.extractUser(token).getUsername());
        System.out.println(user.get().getUsername());
        if(token!=null){
            //System.out.println(moduleService.findModuleByGroup(user.get().getUser_group().getgId()));
            return user.get();
        }else{
            return null;
        }

    }


    public static final String endpoint = "http://ip-api.com/json";




    @RequestMapping(value="/list", method = RequestMethod.GET)
    public List<User> getList(){
        return userService.getListUser();

    }


  /*  @RequestMapping(value = "/resp", method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public List<ModuleDTO> getData(@RequestParam(name = "token") String token) {
        final Optional<User> user = userService.findByUsername(jwtTokenUtil.extractUser(token).getUsername());
        System.out.println(user.get().getUsername());
        List<ModuleDTO> result = new ArrayList<>();
        if (token != null && user.isPresent()) {
            Group grp = user.get().getUser_group();

            // Group functions by module and then by submodule
            Map<Object, Map<Module, List<SubModule>>> submodulesByModule = grp.getListe_submodule()
                    .stream()
                    .collect(Collectors.groupingBy(
                            submodule -> submodule.getModule(),
                            Collectors.groupingBy(SubModule::getModule)
                    ));

            for (Map.Entry<Object, Map<Module, List<SubModule>>> moduleEntry : submodulesByModule.entrySet()) {
                Module module = (Module) moduleEntry.getKey();

                ModuleDTO moduleDTO = new ModuleDTO();
                moduleDTO.setId(module.getId());
                moduleDTO.setModuleName(module.getModuleName());

                List<SubModuleDTO> subModuleDTOs = new ArrayList<>();


                // Sort submodules inside each module
                moduleDTO.setListSubModule(subModuleDTOs);

                result.add(moduleDTO);
            }
        }


        return result;
    }*/


    @RequestMapping(value = "/resp", method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public List<ModuleDTO> getData(@RequestParam(name = "token") String token) {
        final Optional<User> user = userService.findByUsername(jwtTokenUtil.extractUser(token).getUsername());

        List<ModuleDTO> result = new ArrayList<>();

        if (token != null && user.isPresent()) {
            Group grp = user.get().getUser_group();

            // Group submodules by module
            Map<com.example.backend.entities.Module, List<SubModule>> submodulesByModule = grp.getListe_submodule()
                    .stream()
                    .collect(Collectors.groupingBy(SubModule::getModule));

            for (Map.Entry<com.example.backend.entities.Module, List<SubModule>> moduleEntry : submodulesByModule.entrySet()) {
                com.example.backend.entities.Module module = moduleEntry.getKey();

                ModuleDTO moduleDTO = new ModuleDTO();
                moduleDTO.setId(module.getId());
                moduleDTO.setModuleName(module.getModuleName());

                List<SubModuleDTO> subModuleDTOs = moduleEntry.getValue().stream()
                        .map(subModule -> {
                            SubModuleDTO subModuleDTO = new SubModuleDTO();
                            subModuleDTO.setId(subModule.getId());
                            subModuleDTO.setSubModuleName(subModule.getSubModuleName());
                            return subModuleDTO;
                        })
                        .collect(Collectors.toList());

                // Sort submodules inside each module
                moduleDTO.setListSubModule(subModuleDTOs);

                result.add(moduleDTO);
            }
        }

        return result;
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest request) {
        // Check if the request contains an email or username
        if (request.getEmail() != null) {
            // Password reset by email
            Optional<User> user = userService.findByEmail(request.getEmail());
            if (user.get() != null) {
                // Generate and send a password reset token to the user's email
                passwordResetService.generateAndSendPasswordResetToken(user.get());
                return ResponseEntity.ok("Password reset link sent to the email address.");
            }
        } else if (request.getUsername() != null) {
            // Password reset by username
            Optional<User> user = userService.findByUsername(request.getUsername());
            if (user.get() != null) {
                // Generate and send a password reset token to the user's email
                passwordResetService.generateAndSendPasswordResetToken(user.get());
                return ResponseEntity.ok("Password reset link sent to the user's email.");
            }
        }

        return ResponseEntity.badRequest().body("Invalid email or username.");
    }

    @PutMapping("/update-password")
    public ResponseEntity<String> updatePassword(@RequestBody PasswordUpdateRequest request) {
        // Call the service to update the pass
        userService.updatePass(request);
        return ResponseEntity.ok("Password updated successfully");
    }

}
