package market.demo.dto.inquiry;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import market.demo.domain.type.InquiryType;

import java.util.List;

@Data
public class InquiryRequest {
    @NotNull
    private Long memberId;

    @NotNull
    private InquiryType inquiryType;

    @NotEmpty
    private String title;

    @NotEmpty
    private String content;

    private List<String> imageUrls;

    private String contactNumber;

    private Boolean notificationEnabled;
}
