package com.gnu.mojadol.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "PAYMENT")
public class Payment {

    @Id
    @Column(name = "receipt_id", nullable = false, length = 50)
    private String receiptId; // 결제 고유번호

    @ManyToOne
    @JoinColumn(name = "user_seq", referencedColumnName = "user_seq", nullable = false)
    private User user;

    @Column(name = "pg", nullable = false, length = 30)
    private String pg; // 결제사

    @Column(name = "method", nullable = false, length = 30)
    private String method; // 결제 방법

    @Column(name = "price", nullable = false)
    private int price; // 가격

    @Column(name = "purchased_at", nullable = false)
    private String purchasedAt; // 결제 시간

    @Column(name = "receipt_url", length = 255)
    private String receiptUrl; // 결제 영수증 URL

    @Column(name = "card_num", length = 20)
    private String cardNum; // 카드 번호

    @Column(name = "card_company", length = 50)
    private String cardCompany; // 카드 회사

    @Column(name = "del", columnDefinition = "ENUM('결제완료', '결제취소') DEFAULT '결제완료'")
    private String del; // 결제 취소 여부
}
