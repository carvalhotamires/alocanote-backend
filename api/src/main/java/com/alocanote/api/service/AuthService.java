package com.alocanote.api.service;

import com.alocanote.api.model.entity.User;
import com.alocanote.api.model.entity.VerificationToken;
import com.alocanote.api.repository.UserRepository;
import com.alocanote.api.repository.VerificationTokenRepository;
import com.alocanote.api.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final SmsService smsService;
    private final VerificationTokenRepository tokenRepository;

    public AuthService(UserRepository userRepository, SmsService smsService, VerificationTokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.smsService = smsService;
        this.tokenRepository = tokenRepository;
    }

    // Gera e envia token recebendo diretamente o telemóvel
    @Transactional
    public void generateAndSendTokenByPhone(String phone) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new BusinessException("Utilizador não encontrado com este telemóvel."));

        // Gera código de 6 dígitos (000000 a 999999)
        String code = String.format("%06d", new Random().nextInt(1000000));

        VerificationToken verificationToken = new VerificationToken(code, user);
        tokenRepository.save(verificationToken);

        // Dispara o SMS simulado
        smsService.sendVerificationSms(user.getPhone(), code);
    }

    // Valida o token associado ao telemóvel
    @Transactional
    public boolean verifyToken(String phone, String tokenStr) {
        VerificationToken verificationToken = tokenRepository.findByToken(tokenStr)
                .orElseThrow(() -> new BusinessException("Código inválido."));

        // Confere se o token pertence ao telemóvel em validação
        if (!verificationToken.getUser().getPhone().equals(phone)) {
            throw new BusinessException("Código não corresponde a este utilizador.");
        }

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException("O código expirou.");
        }

        // Remove ou invalida o token para não ser reutilizado
        tokenRepository.delete(verificationToken);

        return true;
    }
}