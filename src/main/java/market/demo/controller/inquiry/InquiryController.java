package market.demo.controller.inquiry;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import market.demo.domain.member.jwt.TokenProvider;
import market.demo.dto.inquiry.InquiryDetailResponse;
import market.demo.dto.inquiry.InquiryListResponse;
import market.demo.dto.inquiry.InquiryRequest;
import market.demo.service.inquiry.InquiryService;
import org.antlr.v4.runtime.Token;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inquiry")
public class InquiryController {

    private final InquiryService inquiryService;
    private final TokenProvider tokenProvider;

    @PostMapping("/create")
    public ResponseEntity<?> createInquiry(@Valid @RequestBody InquiryRequest inquiryRequest) {
        inquiryService.createInquiry(inquiryRequest);
        return ResponseEntity.ok("문의 등록 성공");
    }

    //19.
    @GetMapping("/list/")
    public ResponseEntity<Page<InquiryListResponse>> getInquiriesByMemberId(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long memberId = tokenProvider.getMemberIdFromCurrentRequest();
        Page<InquiryListResponse> inquiries = inquiryService.getInquiryList(memberId, page, size);
        return ResponseEntity.ok(inquiries);
    }

    @GetMapping("/{inquiryId}/")
    public ResponseEntity<InquiryDetailResponse> getInquiryDetails(@PathVariable Long inquiryId) {
        InquiryDetailResponse inquiryDetail = inquiryService.getInquiryDetails(inquiryId);
        return ResponseEntity.ok(inquiryDetail);
    }
}
