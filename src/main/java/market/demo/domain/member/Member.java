package market.demo.domain.member;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import market.demo.domain.etc.Wishlist;
import market.demo.domain.inquiry.Inquiry;
import market.demo.domain.item.Review;
import market.demo.domain.member.jwt.Authority;
import market.demo.domain.order.Cart;
import market.demo.domain.order.Order;
import market.demo.dto.changememberinfo.ModifyMemberInfoDto;
import market.demo.exception.InvalidPasswordException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Slf4j
@Builder
@AllArgsConstructor
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

    @ManyToMany
    @JoinTable(
            name = "member_authority",
            joinColumns = {@JoinColumn(name = "member_id", referencedColumnName = "member_id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")})
    private Set<Authority> authorities;

    //sns 연동

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private Cart cart;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Inquiry> inquiries = new ArrayList<>();

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;

    @OneToMany(mappedBy = "issuedTo")
    private List<Coupon> coupons = new ArrayList<>();

    //찜목
    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private Wishlist wishlist;


//    @Builder
//    public static Member createMemberWithLoginId(String loginId, String memberName, String email, String password, String phoneNumber) {
//        Member member = new Member();
//        member.loginId = loginId;
//        member.memberName = memberName;
//        member.email = email;
//        member.password = password;
//        member.phoneNumber = phoneNumber;
//        return member;
//    }

    public static Member createMemberWithProviderInfo(String memberName, String email, String loginId, String password, String phoneNumber, String provider, String providerId) {
        Member member = new Member();
        member.memberName = memberName;
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

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    // 테스트 데이터
    public Member(String memberName, String loginId ,String email, String password,String phoneNumber, Address address) {
        this.memberName = memberName;
        this.loginId = loginId;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public void setCart(Cart cart) {
        if (this.cart != null) {
            this.cart.setMember(null);
        }
        this.cart = cart;
        if (cart != null && cart.getMember() != this) {
            cart.setMember(this);
        }
    }

    public void checkInvalidPassword(String password){
        if(!this.password.equals(password))
            throw new InvalidPasswordException("비밀번호가 맞지 않습니다.");
    }

    public void changeMemberInfo(ModifyMemberInfoDto modifyMemberInfoDto){
        if(modifyMemberInfoDto.getNewPassword().isEmpty()) this.password = modifyMemberInfoDto.getPassword();
        else this.password =modifyMemberInfoDto.getNewPassword();

        this.loginId = modifyMemberInfoDto.getLoginId();
        this.email = modifyMemberInfoDto.getEmail();
        this.phoneNumber = modifyMemberInfoDto.getPhoneNumber();
        this.memberName = modifyMemberInfoDto.getMemberName();

    }

}
