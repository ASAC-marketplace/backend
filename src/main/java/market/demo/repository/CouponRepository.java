package market.demo.repository;

import market.demo.domain.member.Coupon;
import market.demo.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByCouponName(String couponName);

    Optional<Coupon> findByIdAndCouponName(Long couponId, String couponName);

    Optional<Coupon> findByCouponNameAndIssuedTo (String couponName, Member member);
}
