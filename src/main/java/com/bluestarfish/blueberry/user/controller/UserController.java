package com.bluestarfish.blueberry.user.controller;

import com.bluestarfish.blueberry.common.dto.ApiSuccessResponse;
import com.bluestarfish.blueberry.user.dto.JoinRequest;
import com.bluestarfish.blueberry.user.dto.PasswordResetRequest;
import com.bluestarfish.blueberry.user.dto.StudyTimeUpdateRequest;
import com.bluestarfish.blueberry.user.dto.UserUpdateRequest;
import com.bluestarfish.blueberry.user.service.StudyTimeService;
import com.bluestarfish.blueberry.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static com.bluestarfish.blueberry.common.handler.ResponseHandler.handleSuccessResponse;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final StudyTimeService studyTimeService;

    @GetMapping("/whoami")
    public ApiSuccessResponse<?> whoami(
            @CookieValue(name = "Authorization") String accessToken
    ) {
        return handleSuccessResponse(userService.getUserByToken(accessToken), HttpStatus.OK);
    }

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

    @PatchMapping(path = "/{userId}", consumes = "multipart/form-data")
    public ApiSuccessResponse<?> update(
            @PathVariable("userId") Long id,
            @ModelAttribute UserUpdateRequest userUpdateRequest,
            @CookieValue(name = "Authorization") String accessToken
    ) throws IOException {
        userService.update(id, userUpdateRequest, accessToken);
        return handleSuccessResponse(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{userId}")
    public ApiSuccessResponse<?> withdraw(
            @PathVariable("userId") Long id,
            @CookieValue(name = "Authorization") String accessToken
    ) {
        userService.withdraw(id, accessToken);
        return handleSuccessResponse(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/nickname")
    public ApiSuccessResponse<?> validateNickname(
            @RequestParam("nickname") String nickname
    ) {
        userService.validateNickname(nickname);
        return handleSuccessResponse(HttpStatus.OK);
    }

    @PatchMapping("/password")
    public ApiSuccessResponse<?> resetPassword(
            @RequestBody PasswordResetRequest passwordResetRequest,
            @CookieValue(name = "Authorization") String accessToken
    ) {
        userService.resetPassword(passwordResetRequest, accessToken);
        return handleSuccessResponse(HttpStatus.NO_CONTENT);
    }
    
    @GetMapping
    public ApiSuccessResponse<?> findUsersByNickname(
            @CookieValue("Authorization") String accessToken,
            @RequestParam("keyword") String keyword
    ) {
        return handleSuccessResponse(userService.searchUsers(accessToken, keyword), HttpStatus.OK);
    }

    @GetMapping("/{userId}/time")
    public ApiSuccessResponse<?> getStudyTime(
            @PathVariable("userId") Long userId
    ) {
        return handleSuccessResponse(userService.getStudyTime(userId), HttpStatus.OK);
    }

    @PatchMapping("/{userId}/time")
    public ApiSuccessResponse<?> updateStudyTime(
            @PathVariable("userId") Long userId,
            @RequestBody StudyTimeUpdateRequest studyTimeUpdateRequest
    ) {
        userService.updateStudyTime(userId, studyTimeUpdateRequest);
        return handleSuccessResponse(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{userId}/ranks")
    public ApiSuccessResponse<?> getRanks(
            @PathVariable Long userId
    ) {
        return handleSuccessResponse(userService.getRanks(userId), HttpStatus.OK);
    }

    @GetMapping("/my/chart/{userId}")
    public ApiSuccessResponse<?> getChartData(
            @PathVariable("userId") Long userId
    ) {
        return handleSuccessResponse(studyTimeService.getChartData(userId), HttpStatus.OK);
    }
}
