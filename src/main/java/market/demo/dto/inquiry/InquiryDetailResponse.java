package market.demo.dto.inquiry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import market.demo.domain.inquiry.Inquiry;
import market.demo.domain.status.InquiryStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InquiryDetailResponse {
    private Long id;
    private String title;
    private String content;
    private List<String> imageUrls;
    private LocalDate registerDate;
    private InquiryStatus status;
    private String contactNumber;
    private Boolean notificationEnabled;

    public InquiryDetailResponse(Inquiry inquiry) {
        this.id = inquiry.getId();
        this.title = inquiry.getTitle();
        this.content = inquiry.getContent();
        this.imageUrls = new ArrayList<>(inquiry.getImageUrls());
        this.registerDate = inquiry.getRegisterDate();
        this.status = inquiry.getStatus();
        this.contactNumber = inquiry.getContactNumber();
        this.notificationEnabled = inquiry.getNotificationEnabled();
    }
}
