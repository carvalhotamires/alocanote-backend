package com.alocanote.api.service;

import com.alocanote.api.dto.request.CreateReservationRequestDTO;
import com.alocanote.api.exception.BusinessException;
import com.alocanote.api.exception.ResourceNotFoundException;
import com.alocanote.api.model.entity.Location;
import com.alocanote.api.model.entity.Notebook;
import com.alocanote.api.model.entity.Reservation;
import com.alocanote.api.model.entity.User;
import com.alocanote.api.model.enums.NotebookStatus;
import com.alocanote.api.model.enums.ReservationStatus;
import com.alocanote.api.repository.LocationRepository;
import com.alocanote.api.repository.ReservationRepository;
import com.alocanote.api.repository.NotebookRepository;
import com.alocanote.api.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final NotebookService notebookService;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              NotebookService notebookService,
                              UserRepository userRepository,
                              LocationRepository locationRepository) {
        this.reservationRepository = reservationRepository;
        this.notebookService = notebookService;
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
    }

    @Transactional
    public Reservation createReservation(CreateReservationRequestDTO dto, String userEmail) {
        // Validação da ordem das datas
        if (dto.endTime().isBefore(dto.startTime())) {
            throw new BusinessException("A data de término deve ser posterior à data de início.");
        }

        Notebook notebook = notebookService.findById(dto.notebookId());

        if (notebook.getStatus() == NotebookStatus.MANUTENCAO) {
            throw new BusinessException("Este notebook está em manutenção e não pode ser reservado.");
        }

        // Verifica conflitos de horário para o mesmo notebook no período solicitado
        List<Reservation> conflitos = reservationRepository.findConflictingReservations(
                dto.notebookId(),
                dto.startTime(),
                dto.endTime(),
                ReservationStatus.AGENDADO
        );

        if (!conflitos.isEmpty()) {
            throw new BusinessException("Este notebook já possui um agendamento conflitante para este intervalo de horário.");
        }

        // Recupera o usuário logado com base no subject do token JWT
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado não encontrado."));

        // Recupera a localização indicada no DTO
        Location location = locationRepository.findById(dto.locationId())
                .orElseThrow(() -> new ResourceNotFoundException("Local de retirada não encontrado com o ID: " + dto.locationId()));

        Reservation reservation = Reservation.builder()
                .notebook(notebook)
                .user(user)
                .location(location)
                .departureDateTime(dto.startTime())
                .returnDateTime(dto.endTime())
                .purpose(dto.purpose() != null && !dto.purpose().isBlank() ? dto.purpose() : "Uso padrão")
                .status(ReservationStatus.AGENDADO)
                .build();

        return reservationRepository.save(reservation);
    }
}