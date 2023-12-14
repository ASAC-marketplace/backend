package market.demo.service.inquiry;

import lombok.RequiredArgsConstructor;
import market.demo.domain.inquiry.Inquiry;
import market.demo.domain.member.Member;
import market.demo.domain.status.InquiryStatus;
import market.demo.dto.inquiry.InquiryDetailResponse;
import market.demo.dto.inquiry.InquiryListResponse;
import market.demo.dto.inquiry.InquiryRequest;
import market.demo.exception.EntityNotFoundException;
import market.demo.exception.InvalidDataException;
import market.demo.exception.MemberNotFoundException;
import market.demo.repository.InquiryRepository;
import market.demo.repository.MemberRepository;
import market.demo.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final OrderService orderService;
    private final MemberRepository memberRepository;
    public void createInquiry(InquiryRequest inquiryRequest) {
        validateInquiryRequest(inquiryRequest);
        Member member = memberRepository.findById(inquiryRequest.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException("회원이 존재하지 않습니다"));

        Inquiry inquiry = new Inquiry(
                member,
                inquiryRequest.getInquiryType(),
                inquiryRequest.getTitle(),
                inquiryRequest.getContent(),
                inquiryRequest.getImageUrls(),
                inquiryRequest.getContactNumber(),
                inquiryRequest.getNotificationEnabled(),
                LocalDate.now(),
                InquiryStatus.답변대기
        );
        inquiryRepository.save(inquiry);
    }

    private void validateInquiryRequest(InquiryRequest inquiryRequest) {
        if (inquiryRequest == null || !StringUtils.hasText(inquiryRequest.getTitle()) || !StringUtils.hasText(inquiryRequest.getContent())) {
            throw new InvalidDataException("InquiryRequest의 필수 데이터가 누락되었습니다.");
        }
    }

    //리스트 조회
    //제목, 작성일, 답변상태
    public Page<InquiryListResponse> getInquiryList(Long memberId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Inquiry> inquiries = inquiryRepository.findByMemberId(memberId, pageable);
        return inquiries.map(InquiryListResponse::from);
    }

    //디테일 조회
    public InquiryDetailResponse getInquiryDetails(Long inquiryId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new EntityNotFoundException("문의를 찾을 수 없습니다."));

        return new InquiryDetailResponse(inquiry);
    }

    @Transactional(readOnly = true)
    public Inquiry getInquiryById(Long id) {
        return inquiryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("id Inquiry를 찾을 수 없습니다." + id));
    }
//    @Transactional
//    public void deleteInquiry()
}
