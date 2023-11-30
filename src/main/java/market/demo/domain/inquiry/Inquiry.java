package market.demo.domain.inquiry;

import jakarta.persistence.*;
import lombok.Getter;
import market.demo.domain.member.Member;
import market.demo.domain.type.InquiryType;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
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

    private String contactNumber;
    private boolean notificationEnabled;
}
