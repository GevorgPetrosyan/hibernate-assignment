package com.egs.hibernate.rest;


import com.egs.hibernate.dto.UserDTO;
import com.egs.hibernate.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user/")
@RequiredArgsConstructor
@Tag(name = "User Resource", description = "The User API with documentation annotations")
public class UserResource {
    private final UserService userService;

    @Operation(summary = "Generate users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users have been successfully generated")})
    @PostMapping("generate/{count}")
    public void initiateCountries(@PathVariable int count){
        userService.generateUsers(count);
    }
//
//    @Operation(summary = "Show users")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Users list have been successfully shown")})
//    @GetMapping
//    public ResponseEntity<List<UserDTO>> getAllUsers(){
//        return ResponseEntity.ok(userService.getAllUsers());
//    }
//
//    @Operation(summary = "Show users")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Users list have been successfully shown")})
//    @GetMapping("{columnName}")
//    public ResponseEntity<List<UserDTO>> filterUsers(@PathVariable String columnName){
//        return ResponseEntity.ok(userService.usersFilter(columnName));
//    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200")})
    @GetMapping("all")
    public ResponseEntity<Page<ResponseUser>> getAll(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "200") Integer pageSize,
            @RequestParam(defaultValue = "username") String sortBy) {
        Page<ResponseUser> list = userService.getAll(pageNo, pageSize, sortBy);
        return new ResponseEntity<>(list, new HttpHeaders(), HttpStatus.OK);
    }
}
