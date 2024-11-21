package com.gnu.mojadol.service;

import com.gnu.mojadol.dto.MailDto;

public interface MailService {

    void mailSend(MailDto mailDto);
}
