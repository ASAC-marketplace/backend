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
    public Member(String loginId, String memberName, String email, String password, String phoneNumber) {
        this.loginId = loginId;
        this.memberName = memberName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    public Member() {

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
