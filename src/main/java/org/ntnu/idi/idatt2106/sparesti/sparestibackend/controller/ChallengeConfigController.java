package org.ntnu.idi.idatt2106.sparesti.sparestibackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ntnu.idi.idatt2106.sparesti.sparestibackend.dto.ChallengeConfigDTO;
import org.ntnu.idi.idatt2106.sparesti.sparestibackend.exception.BadInputException;
import org.ntnu.idi.idatt2106.sparesti.sparestibackend.exception.ChallengeConfigNotFoundException;
import org.ntnu.idi.idatt2106.sparesti.sparestibackend.exception.UserNotFoundException;
import org.ntnu.idi.idatt2106.sparesti.sparestibackend.service.UserConfigService;
import org.ntnu.idi.idatt2106.sparesti.sparestibackend.util.ApplicationUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/users/me/config/challenge")
public class ChallengeConfigController {

    private final UserConfigService userConfigService;

    @Operation(
            summary = "Create challenge config",
            description = "Creates a new challenge config for the authenticated user.")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Challenge config created"),
                @ApiResponse(responseCode = "404", description = "User not found"),
                @ApiResponse(responseCode = "400", description = "Bad input")
            })
    @PostMapping
    public ResponseEntity<ChallengeConfigDTO> createChallengeConfig(
            @Parameter(description = "Challenge config details to create") @Valid @RequestBody
                    ChallengeConfigDTO challengeConfigDTO,
            BindingResult bindingResult,
            @Parameter(description = "Details of the authenticated user") @AuthenticationPrincipal
                    UserDetails userDetails)
            throws UserNotFoundException, BadInputException {
        if (bindingResult.hasErrors()) {
            throw new BadInputException(ApplicationUtil.BINDING_RESULT_ERROR);
        }
        log.info("Received request to create challenge config: {}", challengeConfigDTO);
        ChallengeConfigDTO newConfig =
                userConfigService.createChallengeConfig(
                        userDetails.getUsername(), challengeConfigDTO);
        log.info("Successfully created challenge config: {}", newConfig);
        return ResponseEntity.ok(newConfig);
    }

    @Operation(
            summary = "Get challenge config",
            description = "Retrieves the challenge config for the authenticated user.")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Challenge config found"),
                @ApiResponse(responseCode = "404", description = "Challenge config not found")
            })
    @GetMapping
    public ResponseEntity<ChallengeConfigDTO> getChallengeConfig(
            @Parameter(description = "Details of the authenticated user") @AuthenticationPrincipal
                    UserDetails userDetails)
            throws ChallengeConfigNotFoundException {
        log.info(
                "Received request to get challenge config for username: {}",
                userDetails.getUsername());
        ChallengeConfigDTO config = userConfigService.getChallengeConfig(userDetails.getUsername());
        log.info("Successfully retrieved challenge config: {}", config);
        return ResponseEntity.ok(config);
    }

    @Operation(
            summary = "Update challenge config",
            description = "Updates the challenge config for the authenticated user.")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Challenge config updated"),
                @ApiResponse(responseCode = "404", description = "Challenge config not found"),
                @ApiResponse(responseCode = "400", description = "Bad input")
            })
    @PutMapping
    public ResponseEntity<ChallengeConfigDTO> updateChallengeConfig(
            @Parameter(description = "Updated challenge config details") @Valid @RequestBody
                    ChallengeConfigDTO challengeConfigDTO,
            BindingResult bindingResult,
            @Parameter(description = "Details of the authenticated user") @AuthenticationPrincipal
                    UserDetails userDetails)
            throws ChallengeConfigNotFoundException, BadInputException {
        log.info("Received request to update challenge config to: {}", challengeConfigDTO);
        if (bindingResult.hasErrors()) {
            throw new BadInputException(ApplicationUtil.BINDING_RESULT_ERROR);
        }

        ChallengeConfigDTO updatedConfig =
                userConfigService.updateChallengeConfig(
                        userDetails.getUsername(), challengeConfigDTO);
        log.info("Successfully updated challenge config to: {}", updatedConfig);
        return ResponseEntity.ok(updatedConfig);
    }
}
