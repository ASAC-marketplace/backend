package market.demo.repository;

import market.demo.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByLoginId(String loginId);
    boolean existsByEmail(String email);
    boolean existsByLoginIdAndEmail(String loginId, String email);
    Member findByLoginId(String loginId);
    Optional<Member> findByEmail(String email);

}
