package com.cartechindia.controller;

import com.cartechindia.entity.User;
import com.cartechindia.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    //@PreAuthorize("hasAnyRole('HELPDESK','ADMIN')")
    public List<User> getAll() { return userService.getAll(); }

    @GetMapping("/{id}")
    //@PreAuthorize("hasAnyRole('HELPDESK','ADMIN')")
    public User getById(@PathVariable Long id) { return userService.getById(id); }

    @PostMapping
    //@PreAuthorize("hasRole('ADMIN')")
    public User create(@RequestBody User user) { return userService.save(user); }

    @PutMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public User update(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        return userService.save(user);
    }

    @DeleteMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) { userService.delete(id); }
}
