package market.demo.controller.inquiry;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import market.demo.dto.inquiry.InquiryDetailResponse;
import market.demo.dto.inquiry.InquiryListResponse;
import market.demo.dto.inquiry.InquiryRequest;
import market.demo.service.inquiry.InquiryService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inquiry")
public class InquiryController {

    private final InquiryService inquiryService;

    @PostMapping("/create")
    public ResponseEntity<?> createInquiry(@Valid @RequestBody InquiryRequest inquiryRequest) {
        inquiryService.createInquiry(inquiryRequest);
        return ResponseEntity.ok("문의 등록 성공");
    }

    @GetMapping("/list/{memberId}")
    public ResponseEntity<Page<InquiryListResponse>> getInquiriesByMemberId(
            @PathVariable Long memberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<InquiryListResponse> inquiries = inquiryService.getInquiryList(memberId, page, size);
        return ResponseEntity.ok(inquiries);
    }

    @GetMapping("/{inquiryId}/")
    public ResponseEntity<InquiryDetailResponse> getInquiryDetails(@PathVariable Long inquiryId) {
        InquiryDetailResponse inquiryDetail = inquiryService.getInquiryDetails(inquiryId);
        return ResponseEntity.ok(inquiryDetail);
    }

//    @GetMapping()
//    public ResponseEntity<> 리스트 조회 () {
//
//    }
//    @GetMapping()
//    public ResponseEntity<> 디테일 조회 () {
//
//    }
}
