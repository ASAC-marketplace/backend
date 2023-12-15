package market.demo.domain.inquiry;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import market.demo.domain.member.Member;
import market.demo.domain.status.InquiryStatus;
import market.demo.domain.type.InquiryType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Inquiry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Inquiry_id") //erd 수정
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private InquiryType inquiryType;

    private String title;
    private String content;

    @ElementCollection
    @CollectionTable(name = "inquiry_images", joinColumns = @JoinColumn(name = "inquiry_id"))
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>();

    //작성일
    private LocalDate registerDate;

    //답변상태
    @Enumerated(EnumType.STRING)
    private InquiryStatus status;

    private String contactNumber;
    private Boolean notificationEnabled;

    //createInquiry - InquiryService
    public Inquiry(Member member, InquiryType inquiryType, String title, String content,
                   List<String> imageUrls, String contactNumber, Boolean notificationEnabled, LocalDate registerDate, InquiryStatus status) {
        this.member = member;
        this.inquiryType = inquiryType;
        this.title = title;
        this.content = content;
        this.imageUrls = imageUrls;
        this.contactNumber = contactNumber;
        this.notificationEnabled = notificationEnabled;
        this.registerDate = registerDate;
        this.status = status;
    }

    public Inquiry(String title, LocalDate registerDate, InquiryStatus status) {
        this.title = title;
        this.registerDate = registerDate;
        this.status = status;
    }
}
