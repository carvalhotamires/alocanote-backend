package com.alocanote.api.controller;

import com.alocanote.api.dto.request.CreateReservationRequestDTO;
import com.alocanote.api.model.entity.Reservation;
import com.alocanote.api.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/agendar")
    public ResponseEntity<Reservation> agendarNotebook(
            @Valid @RequestBody CreateReservationRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        // userDetails.getUsername() contém o e-mail do usuário extraído do JWT pelo SecurityFilter
        Reservation novaReserva = reservationService.createReservation(dto, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(novaReserva);
    }
}