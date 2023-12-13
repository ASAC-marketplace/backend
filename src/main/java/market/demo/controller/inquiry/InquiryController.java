package market.demo.controller.inquiry;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import market.demo.dto.inquiry.InquiryRequest;
import market.demo.service.inquiry.InquiryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

//    @GetMapping()
//    public ResponseEntity<> 리스트 조회 () {
//
//    }
//    @GetMapping()
//    public ResponseEntity<> 디테일 조회 () {
//
//    }
}
