package market.demo.domain.member;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import market.demo.domain.etc.Wishlist;
import market.demo.domain.member.jwt.Authority;
import market.demo.domain.order.Cart;
import market.demo.domain.status.AgeStatus;
import market.demo.domain.status.GenderStatus;
import market.demo.domain.type.PromotionType;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class MemberTestDataCreator {
    private final InitMemberService initMemberService;

    public MemberTestDataCreator(InitMemberService initMemberService) {
        this.initMemberService = initMemberService;
    }

    public void init() {
        initMemberService.createMembersAndAuthorities();
        initMemberService.createTestMembers(200);
    }

    @Component
    static class InitMemberService {
        @PersistenceContext
        private EntityManager em;

        private Authority roleUser;
        private Authority roleAdmin;

        @Transactional
        public void createMembersAndAuthorities() {
            roleUser = new Authority("ROLE_USER");
            roleAdmin = new Authority("ROLE_ADMIN");
            em.persist(roleUser);
            em.persist(roleAdmin);
        }

        @Transactional
        public void createTestMembers(int numberOfMembers) {
            for (int i = 0; i < numberOfMembers; i++) {
                Member member = createTestMember(i);
                em.persist(member);

                Set<Authority> authorities = new HashSet<>();
                authorities.add(roleUser);
                if (ThreadLocalRandom.current().nextBoolean()) {
                    authorities.add(roleAdmin);
                }

                Cart cart = new Cart();
                cart.setMember(member);
                member.setCart(cart);

                //wishlist 추가
                member.setWishlist(new Wishlist(member));

                member.setAuthorities(authorities);
            }
        }

        private Member createTestMember(int i) {
            String name = getRandomName();
            String email = "user" + ThreadLocalRandom.current().nextInt(1000, 9999) + "@example.com";
            String loginId = "user" + i;
            String password = "test1234"; // 모든 테스트 사용자에게 동일한 비밀번호 사용
            return new Member(name, loginId, email, password,
                    getRandomPhoneNumber(), getRandomAddress(), getRandomAge(), getRandomGender());
        }


        private String getRandomName() {
            String[] firstNames = {"김", "이", "박", "최", "정"};
            String[] lastNames = {"준호", "은지", "민수", "서연", "현우"};
            return firstNames[ThreadLocalRandom.current().nextInt(firstNames.length)] +
                    lastNames[ThreadLocalRandom.current().nextInt(lastNames.length)];
        }

        private String getRandomPhoneNumber() {
            // 전화번호 형식: "010-xxxx-xxxx"
            return "010-" +
                    ThreadLocalRandom.current().nextInt(1000, 10000) + "-" +
                    ThreadLocalRandom.current().nextInt(1000, 10000);
        }

        private Address getRandomAddress() {
            String[] cities = {"서울", "부산", "대구", "인천", "광주", "대전", "울산"};
            String[] streets = {"강남대로", "역삼로", "범어대로", "중앙대로", "첨단로", "태평로", "문화로"};
            String zipcode = String.format("%05d", ThreadLocalRandom.current().nextInt(10000, 99999));
            return new Address(cities[ThreadLocalRandom.current().nextInt(cities.length)],
                    streets[ThreadLocalRandom.current().nextInt(streets.length)],
                    zipcode);
        }

        private AgeStatus getRandomAge(){
            AgeStatus[] ageStatuses = AgeStatus.values();
            int randomIndex = ThreadLocalRandom.current().nextInt(ageStatuses.length);
            return ageStatuses[randomIndex];
        }

        private GenderStatus getRandomGender(){
            GenderStatus[] genderStatuses = GenderStatus.values();
            int randomIndex = ThreadLocalRandom.current().nextInt(genderStatuses.length);
            return genderStatuses[randomIndex];
        }
    }
}
