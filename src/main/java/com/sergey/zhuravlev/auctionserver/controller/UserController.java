package com.sergey.zhuravlev.auctionserver.controller;

import com.sergey.zhuravlev.auctionserver.model.User;
import com.sergey.zhuravlev.auctionserver.service.SecurityService;
import com.sergey.zhuravlev.auctionserver.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(LotController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<User> getMyProfile(){
        if (getAuthenticationUser()==null) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            else return
                new ResponseEntity<>(
                        userService.findByUsernameWithoutPassword(getAuthenticationUser().getUsername()),
                        HttpStatus.OK);
    }

    @RequestMapping(value = "/users/{username}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<User> getAnotherProfile(@PathVariable("username") String username){
        User user = userService.findByUsernameWithoutPassword(username);
        if (user == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            else return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String registration(@RequestBody User user) {
        logger.debug("Add user = " + user.toString());
        if ((user.getUsername() == null || userService.findByUsername(user.getUsername()) != null)
                || (user.getUsername().length() < 6 || user.getUsername().length() > 32))  {
            logger.debug("Error username");
            return "error.username";
        }

        if (user.getPassword() == null || (user.getPassword().length() < 6 || user.getPassword().length() > 32)
                || !user.getConfirmPassword().equals(user.getPassword()))  {
            logger.debug("Registration Password Error: password(" + user.getPassword() + "), confPassword(" + user.getConfirmPassword() + ");");
            return "error.password";
        }

        logger.debug("Save " + user);
        userService.save(user);

        securityService.autoLogin(user.getUsername(), user.getConfirmPassword());

        return "found";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ResponseEntity<User> login(String error) {
        if (error != null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Secured({"ROLE_USER"})
    @RequestMapping(value = "/profile/token", method = RequestMethod.POST)
    public void registrationNotificationKey(@RequestBody String token) {
        if (!token.isEmpty()) {
            User user = userService.findByUsername(getAuthenticationUser().getUsername());
            if (user != null) {
                user.setNotificationToken(token);
                userService.update(user);
            }
        }
    }

    @Secured({"ROLE_USER"})
    @RequestMapping(value = "/profile/token/unregister", method = RequestMethod.GET)
    public void unregisterNotificationKey() {
        User user = userService.findByUsername(getAuthenticationUser().getUsername());
        if (user != null) {
            userService.deleteNotificationToken(user.getId());
        }
    }





    private UserDetails getAuthenticationUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            return (UserDetails) auth.getPrincipal();
        } else return null;
    }

}
