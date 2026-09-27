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

import java.util.List;

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

        Reservation novaReserva = reservationService.createReservation(dto, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(novaReserva);
    }

    @GetMapping
    public ResponseEntity<List<Reservation>> listarMinhasReservas(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<Reservation> reservas = reservationService.findMyReservations(userDetails.getUsername());
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> buscarPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        Reservation reserva = reservationService.findById(id, userDetails.getUsername());
        return ResponseEntity.ok(reserva);
    }

    @PatchMapping("/{id}/check-in")
    public ResponseEntity<Reservation> checkIn(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        Reservation reservaAtualizada = reservationService.checkIn(id, userDetails.getUsername());
        return ResponseEntity.ok(reservaAtualizada);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Reservation> cancelar(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        Reservation reservaAtualizada = reservationService.cancelReservation(id, userDetails.getUsername());
        return ResponseEntity.ok(reservaAtualizada);
    }
}