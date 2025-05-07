package com.matchango.scoutingservice.infrastructure.web;

import com.matchango.scoutingservice.application.ScoutingReportService;
import com.matchango.scoutingservice.domain.model.Position;
import com.matchango.scoutingservice.infrastructure.web.dto.PlayerWithRatingDto;
import com.matchango.scoutingservice.infrastructure.web.dto.ApiResponse;
import com.matchango.scoutingservice.infrastructure.web.dto.CreateScoutingReportDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class ScoutingReportController {

    private final ScoutingReportService scoutingReportService;

    @PreAuthorize("hasRole('SCOUT')")
    @PostMapping("/reports")
    public ResponseEntity<ApiResponse> createReport(@Valid @RequestBody CreateScoutingReportDto createScoutingReportDto) {
        try {
            // TODO: Ref - move this check and throw error to Service
            String positionString = createScoutingReportDto.getPosition();
            Position position;
            if (positionString != null) {
                positionString = positionString.toUpperCase();

                try {
                    position = Position.valueOf(positionString);
                } catch (IllegalArgumentException e) {
                    position = null;
                }
            }
            else {
                position = null;
            }

            scoutingReportService.createReport(
                    createScoutingReportDto.getLastName(),
                    createScoutingReportDto.getFirstName(),
                    createScoutingReportDto.getAge(),
                    position,
                    createScoutingReportDto.getScoutUsername(),
                    createScoutingReportDto.getMatch(),
                    createScoutingReportDto.getObservation(),
                    createScoutingReportDto.getTechnicalRating()
            );

            ApiResponse response = ApiResponse.builder()
                    .status("success")
                    .message("Report created successfully.")
                    .build();
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            ApiResponse response = ApiResponse.builder()
                    .status("error")
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/players/search")
    public ResponseEntity<ApiResponse> searchPlayers(
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) Double minRating
    ) {
        List<PlayerWithRatingDto> result = scoutingReportService.findPlayersWithFiltres(age, position, minRating);
        ApiResponse response = ApiResponse.builder()
                .status("success")
                .message("Players fetched successfully")
                .data(result)
                .build();
        return ResponseEntity.ok(response);
    }

}
