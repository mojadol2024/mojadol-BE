package com.gnu.mojadol.dto;

import com.gnu.mojadol.entity.User;
import lombok.Data;

@Data
public class PaymentDto {
    private String receiptId; 		// 결제 고유번호
    private User user; 			// customer 테이블에서 참조
    private String pg; 				// 결제사
    private String method; 			// 결제 방법 (카카오페이, 네이버페이, 페이코)
    private int price; 				// 가격
    private String purchasedAt; 	// 결제시간
    private String receiptUrl;		// 결제 영수증 URL
    private String cardNum; 		// 카드 번호
    private String cardCompany; 	// 카드 회사
    private String del; 			// 결제 취소 여부 (결제완료, 결제취소..)
}
