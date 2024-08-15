package com.bluestarfish.blueberry.user.controller;

import static com.bluestarfish.blueberry.common.handler.ResponseHandler.handleSuccessResponse;

import com.bluestarfish.blueberry.common.dto.ApiSuccessResponse;
import com.bluestarfish.blueberry.user.dto.JoinRequest;
import com.bluestarfish.blueberry.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ApiSuccessResponse<?> join(
            @RequestBody JoinRequest joinRequest
    ) {
        userService.join(joinRequest);
        return handleSuccessResponse(HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ApiSuccessResponse<?> findById(
            @PathVariable("userId") Long id
    ) {
        return handleSuccessResponse(userService.findById(id), HttpStatus.OK);
    }
}
