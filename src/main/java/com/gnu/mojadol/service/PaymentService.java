package com.gnu.mojadol.service;

import com.gnu.mojadol.dto.PaymentDto;

public interface PaymentService {

    void add(PaymentDto paymentDto);

    void cancel(String receiptId);

}
