package com.gnu.mojadol.repository;

import com.gnu.mojadol.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

    Payment findByReceiptId(String receiptId);
}
