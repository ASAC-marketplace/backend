package market.demo.domain.member;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import market.demo.domain.etc.Wishlist;
import market.demo.domain.inquiry.Inquiry;
import market.demo.domain.item.Review;
import market.demo.domain.order.Cart;
import market.demo.domain.order.Order;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Slf4j
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String loginId;

    private String memberName;

    private String email;

    private String password;

    private String gender;
    private String ageRange;
    private String phoneNumber;
    private LocalDate birthday;
    private String provider;
    private String providerId;
    //Set<Role> roles;

    //sns 연동

    @OneToOne(mappedBy = "member")
    private Cart cart;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Inquiry> inquiries = new ArrayList<>();

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "member")
    private List<Order> orders;

    @OneToMany(mappedBy = "member")
    private List<Review> reviews;

    @OneToMany(mappedBy = "issuedTo")
    private List<Coupon> coupons = new ArrayList<>();

    //찜목
    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private Wishlist wishlist;

    @Builder
    public static Member createMemberWithProviderInfo(String email, String loginId, String password, String phoneNumber, String provider, String providerId) {
        Member member = new Member();
        member.email = email;
        member.loginId = loginId;
        member.password = password;
        member.phoneNumber = phoneNumber;
        member.provider = provider;
        member.providerId = providerId;
        return member;
    }

    public Member() {

    }

    public Member(String email) {
        this.email = email;
    }

    // 소셜 로그인을 위한 생성자
    public Member(String email, String memberName, String provider, String providerId) {
        log.info("Creating Member with email: {}", email);
        this.email = email;
        this.memberName = memberName;
        this.provider = provider;
        this.providerId = providerId;

        if (email == null) {
            log.error("Email is null for Member");
        }
    }

    public void updatePassword(String newPassword, PasswordEncoder passwordEncoder) {
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("새 비밀번호는 비어 있을 수 없습니다.");
        }

        //비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(newPassword);
        this.password = encodedPassword;
    }

    public void updateSocialLoginInfo(String provider, String providerId) {
        this.provider = provider;
        this.providerId = providerId;
    }
}
