package com.dh.demo.service;

import com.dh.demo.dto.RegistrationEmailDTO;
import com.dh.demo.dto.request.EmailRequestDTO;

public interface IEmailService {

    void sendSimpleEmail(EmailRequestDTO emailRequest);
    void sendHtmlEmail(EmailRequestDTO emailRequest);
    void sendRegistrationConfirmation(RegistrationEmailDTO data);
    void sendBookingConfirmation(String to, String guestName, String hotelName, String checkIn, String checkOut);
}