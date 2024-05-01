package org.ntnu.idi.idatt2106.sparesti.sparestibackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ntnu.idi.idatt2106.sparesti.sparestibackend.dto.challenge.ChallengeCreateDTO;
import org.ntnu.idi.idatt2106.sparesti.sparestibackend.dto.challenge.ChallengeDTO;
import org.ntnu.idi.idatt2106.sparesti.sparestibackend.dto.challenge.ChallengeUpdateDTO;
import org.ntnu.idi.idatt2106.sparesti.sparestibackend.exception.BadInputException;
import org.ntnu.idi.idatt2106.sparesti.sparestibackend.exception.ChallengeNotFoundException;
import org.ntnu.idi.idatt2106.sparesti.sparestibackend.exception.UserNotFoundException;
import org.ntnu.idi.idatt2106.sparesti.sparestibackend.exception.validation.ObjectNotValidException;
import org.ntnu.idi.idatt2106.sparesti.sparestibackend.model.User;
import org.ntnu.idi.idatt2106.sparesti.sparestibackend.service.ChallengeService;
import org.ntnu.idi.idatt2106.sparesti.sparestibackend.service.FileSystemStorageService;
import org.ntnu.idi.idatt2106.sparesti.sparestibackend.service.UserService;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/challenges")
@Tag(name = "Challenges", description = "Endpoints for managing user challenges")
@RequiredArgsConstructor
public class ChallengeController {

    private final UserService userService;

    private final ChallengeService challengeService;

    private final FileSystemStorageService fileSystemStorageService;

    @Operation(
            summary = "Get user challenges",
            description = "Retrieve challenges associated with the authenticated user.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Challenges found",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ChallengeDTO.class))
                        }),
                @ApiResponse(responseCode = "404", description = "Challenges or user not found")
            })
    @GetMapping
    public ResponseEntity<Page<ChallengeDTO>> getUserChallenges(
            @Parameter(description = "Pageable object for pagination") Pageable pageable,
            @Parameter(description = "Details of the authenticated user") @AuthenticationPrincipal
                    UserDetails userDetails)
            throws ChallengeNotFoundException, UserNotFoundException {
        log.info("Received GET request for challenges by username: {}", userDetails.getUsername());
        User user = getUser(userDetails);

        Page<ChallengeDTO> challenges = challengeService.getChallengesByUser(user, pageable);
        log.info("Retrieved challenges: {}", challenges);
        return ResponseEntity.ok(challenges);
    }

    @Operation(
            summary = "Get active challenges",
            description = "Retrieve active challenges associated with the authenticated user.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Active challenges found",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ChallengeDTO.class))
                        }),
                @ApiResponse(responseCode = "404", description = "User not found")
            })
    @GetMapping("/active")
    public ResponseEntity<Page<ChallengeDTO>> getActiveChallenges(
            Pageable pageable, @AuthenticationPrincipal UserDetails userDetails) {
        log.info(
                "Received GET request for active challenges by user: {}",
                userDetails.getUsername());
        User user = userService.findUserByUsername(userDetails.getUsername());
        return ResponseEntity.ok(challengeService.getActiveChallenges(user, pageable));
    }

    @Operation(
            summary = "Get completed challenges",
            description = "Retrieve completed challenges associated with the authenticated user.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Completed challenges found",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ChallengeDTO.class))
                        }),
                @ApiResponse(responseCode = "404", description = "User not found")
            })
    @GetMapping("/completed")
    public ResponseEntity<Page<ChallengeDTO>> getCompletedChallenges(
            Pageable pageable, @AuthenticationPrincipal UserDetails userDetails) {
        log.info(
                "Received GET request for completed challenges by user: {}",
                userDetails.getUsername());
        User user = getUser(userDetails);
        return ResponseEntity.ok(challengeService.getCompletedChallenges(user, pageable));
    }

    @Operation(
            summary = "Get user challenge",
            description = "Retrieve a specific challenge for the authenticated user by ID.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Challenge found",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ChallengeDTO.class))
                        }),
                @ApiResponse(responseCode = "404", description = "Challenge or user not found")
            })
    @GetMapping("/{id}")
    public ResponseEntity<ChallengeDTO> getUserChallenge(
            @Parameter(description = "Details of the authenticated user") @AuthenticationPrincipal
                    UserDetails userDetails,
            @Parameter(description = "ID of the challenge to retrieve", example = "123")
                    @PathVariable
                    Long id)
            throws ChallengeNotFoundException, UserNotFoundException {
        log.info("Received GET request for challenge with id: {}", id);
        User user = getUser(userDetails);
        ChallengeDTO retrievedChallenge = challengeService.getChallenge(id, user);
        log.info("Retrieved challenge: {}", retrievedChallenge);
        return ResponseEntity.ok(retrievedChallenge);
    }

    @Operation(
            summary = "Create challenge",
            description = "Creates a new challenge for the authenticated user.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Challenge created",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ChallengeDTO.class))
                        }),
                @ApiResponse(responseCode = "404", description = "User not found"),
                @ApiResponse(responseCode = "400", description = "Bad input")
            })
    @PostMapping
    public ResponseEntity<ChallengeDTO> createChallenge(
            @Parameter(description = "Challenge details to create") @RequestBody
                    ChallengeCreateDTO challengeCreateDTO,
            @Parameter(description = "Details of the authenticated user") @AuthenticationPrincipal
                    UserDetails userDetails)
            throws ChallengeNotFoundException,
                    UserNotFoundException,
                    BadInputException,
                    ObjectNotValidException {
        log.info(
                "Received POST request for challenge: " + challengeCreateDTO + " for user: {}",
                userDetails.getUsername());
        User user = getUser(userDetails);

        ChallengeDTO createdChallenge = challengeService.save(challengeCreateDTO, user);
        log.info("Created challenge: {}", createdChallenge);
        return ResponseEntity.ok(createdChallenge);
    }

    @Operation(
            summary = "Complete challenge",
            description = "Marks a challenge as completed for the authenticated user.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Challenge completed",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ChallengeDTO.class))
                        }),
                @ApiResponse(responseCode = "404", description = "Challenge not found")
            })
    @PutMapping("/{id}/complete")
    public ResponseEntity<ChallengeDTO> completeChallenge(
            @Parameter(description = "ID of the challenge to complete") @PathVariable Long id,
            @Parameter(description = "Details of the authenticated user") @AuthenticationPrincipal
                    UserDetails userDetails) {
        log.info(
                "Received request by user '{}' to complete challenge with id: {}",
                userDetails.getUsername(),
                id);
        User user = getUser(userDetails);
        ChallengeDTO completedChallenge = challengeService.completeChallenge(id, user);
        log.info(
                "Completion status of challenge with id {} set to: {}",
                completedChallenge.id(),
                completedChallenge.completedOn());
        return ResponseEntity.ok(completedChallenge);
    }

    @Operation(
            summary = "Update challenge",
            description = "Updates an existing challenge for the authenticated user.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Challenge updated",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ChallengeDTO.class))
                        }),
                @ApiResponse(responseCode = "404", description = "Challenge or user not found"),
                @ApiResponse(responseCode = "400", description = "Bad input")
            })
    @PutMapping("/{id}")
    public ResponseEntity<ChallengeDTO> updateChallenge(
            @Parameter(description = "ID of the challenge to update") @PathVariable Long id,
            @Parameter(description = "Updated challenge details") @RequestBody
                    ChallengeUpdateDTO challengeUpdateDTO,
            @Parameter(description = "Details of the authenticated user") @AuthenticationPrincipal
                    UserDetails userDetails)
            throws ChallengeNotFoundException,
                    UserNotFoundException,
                    BadInputException,
                    ObjectNotValidException {
        log.info("Received PUT request for challenge with id: {}", id);
        User user = getUser(userDetails);
        ChallengeDTO updatedChallenge =
                challengeService.updateChallenge(id, challengeUpdateDTO, user);

        log.info("Updated challenge to: {}", updatedChallenge);
        return ResponseEntity.ok(updatedChallenge);
    }

    @Operation(
            summary = "Delete challenge",
            description = "Deletes a specific challenge for the authenticated user by ID.")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "204", description = "Challenge deleted"),
                @ApiResponse(responseCode = "404", description = "Challenge or user not found")
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChallenge(
            @Parameter(description = "ID of the challenge to delete") @NotNull @PathVariable
                    Long id,
            @Parameter(description = "Details of the authenticated user") @AuthenticationPrincipal
                    UserDetails userDetails)
            throws ChallengeNotFoundException, UserNotFoundException {
        log.info("Received DELETE request for challenge with id: {}", id);
        User user = getUser(userDetails);
        challengeService.deleteChallenge(id, user);
        log.info("Deleted challenge with id: {}", id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Generate user challenges",
            description = "Generates challenges based on the configuration set by the user")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Generated challenges",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ChallengeDTO.class))
                        }),
                @ApiResponse(
                        responseCode = "401",
                        description = "Invalid or expired JWT token",
                        content = @Content),
                @ApiResponse(
                        responseCode = "403",
                        description = "No JWT token provided",
                        content = @Content),
                @ApiResponse(
                        responseCode = "404",
                        description = "User config or user not found",
                        content = @Content)
            })
    @GetMapping("/generate")
    public ResponseEntity<List<ChallengeDTO>> generateChallenges(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(challengeService.getGeneratedChallenges(getUser(userDetails)));
    }

    /**
     * Retrieves the user based on the UserDetails.
     *
     * @param userDetails The UserDetails object representing the authenticated user.
     * @return The User object associated with the authenticated user.
     * @throws UserNotFoundException If the user is not found.
     */
    private User getUser(@Parameter(hidden = true) UserDetails userDetails)
            throws UserNotFoundException {
        return userService.findUserByUsername(userDetails.getUsername());
    }

    @PostMapping("/picture")
    public ResponseEntity<String> handleFileUpload(
            @RequestParam String id,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails)
            throws IOException {
        fileSystemStorageService.save(file, id + "-C", userDetails);
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/picture")
    @ResponseBody
    public ResponseEntity<Resource> findFile(
            @RequestParam String id, @AuthenticationPrincipal UserDetails userDetails)
            throws IOException {
        Resource file = fileSystemStorageService.getImage(id + "-C", userDetails);

        if (file == null) return ResponseEntity.notFound().build();

        String mimeType;
        try {
            mimeType = Files.probeContentType(Paths.get(file.getURI()));
        } catch (IOException e) {
            mimeType = "application/octet-stream"; // default MIME type if detection fails
        }

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(mimeType)).body(file);
    }
}
