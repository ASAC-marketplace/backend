package market.demo.dto.inquiry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import market.demo.domain.inquiry.Inquiry;
import market.demo.domain.status.InquiryStatus;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InquiryListResponse {
    private Long inquiryId;
    private String title;
    private LocalDate registerDate;
    private InquiryStatus status;

    public static InquiryListResponse from(Inquiry inquiry) {
        InquiryListResponse response = new InquiryListResponse();
        response.inquiryId = inquiry.getId();
        response.title = inquiry.getTitle();
        response.registerDate = inquiry.getRegisterDate();
        response.status = inquiry.getStatus();
        return response;
    }
<<<<<<< HEAD
=======

//    // 매개변수를 받는 생성자
//    public InquiryListResponse(Inquiry inquiry) {
//        this.id = inquiry.getId();
//        this.title = inquiry.getTitle();
//        this.registerDate = inquiry.getRegisterDate();
//        this.status = inquiry.getStatus();
//    }
>>>>>>> origin/main
}
