package market.demo.dto.inquiry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import market.demo.domain.member.Member;
import market.demo.domain.status.InquiryStatus;
import market.demo.domain.type.InquiryType;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class InquiryResponse {
    private Member member;
    private InquiryType inquiryType;
    private String title;
    private String content;
    private List<String> imageUrls;
    private String contactNumber;
    private Boolean notificationEnabled;
}
