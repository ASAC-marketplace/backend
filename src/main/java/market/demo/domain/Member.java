package market.demo.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

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

    @Builder
    public static Member createMemberWithLoginId(String loginId, String memberName, String email, String password, String phoneNumber) {
        Member member = new Member();
        member.loginId = loginId;
        member.memberName = memberName;
        member.email = email;
        member.password = password;
        member.phoneNumber = phoneNumber;
        return member;
    }

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

    public void updateLoginId(String loginId) {
        this.loginId = loginId;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void updateMemberInfo(String loginId, String password, String phoneNumber) {
        // null 또는 빈 값 체크는 필요에 따라 추가
        this.loginId = loginId;
        this.password = password; // 비밀번호는 암호화하는 로직을 추가하는 것이 좋음
        this.phoneNumber = phoneNumber;
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
