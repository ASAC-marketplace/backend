package market.demo.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
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
}
