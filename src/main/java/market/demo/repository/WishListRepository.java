package market.demo.repository;

import market.demo.domain.etc.Wishlist;
import market.demo.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishListRepository extends JpaRepository<Wishlist, Long> {
    Wishlist findByMember(Member member);
}