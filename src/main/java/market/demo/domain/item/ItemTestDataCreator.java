package market.demo.domain.item;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import market.demo.domain.member.Member;
import market.demo.domain.member.jwt.Authority;
import market.demo.domain.status.ItemStatus;
import market.demo.domain.type.PromotionType;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class ItemTestDataCreator {
    private final InitService initService;

    public ItemTestDataCreator(InitService initService) {
        this.initService = initService;
    }

    public void init() {
        initService.createTestData(100);
//        initService.createMembersAndAuthorities();
    }

    @Component
    static class InitService {
        @PersistenceContext
        private EntityManager em;

//        @Transactional
//        public void createMembersAndAuthorities() {
//            Authority roleUser = new Authority("ROLE_USER");
//            Authority roleAdmin = new Authority("ROLE_ADMIN");
//            em.persist(roleUser);
//            em.persist(roleAdmin);
//        }

        @Transactional
        public void createTestData(int numberOfItems) {
            // 대분류 카테고리 설정
            String[] mainCategories = {"전자제품", "의류", "도서"};
            String[][] subCategories = {
                    {"스마트폰", "노트북", "태블릿"},
                    {"청바지", "셔츠", "자켓"},
                    {"소설", "과학", "역사"}
            };

            for (int i = 0; i < mainCategories.length; i++) {
                Category mainCategory = new Category(mainCategories[i]);
                em.persist(mainCategory);

                for (int j = 0; j < subCategories[i].length; j++) {
                    Category subCategory = new Category(subCategories[i][j], mainCategory);
                    em.persist(subCategory);

                    createItemsForCategory(subCategory, numberOfItems / (mainCategories.length * subCategories[i].length));
                }
            }
        }

        private void createItemsForCategory(Category category, int itemCount) {
            for (int i = 0; i < itemCount; i++) {
                String itemName = generateItemName(category.getName(), i);
                Item item = createItem(itemName, category);
                em.persist(item);

                createReviewsForItem(item, 5);
            }
        }

        private Item createItem(String name, Category category) {
            Integer price = getRandomPrice(10000, 100000);
            Integer discountRate = getRandomDiscountRate(0, 20);
            PromotionType promotionType = getRandomPromotionType();
            LocalDate date = createRandomDate(2021, 2023);
            String brand = getRandomBrandName();
            ItemStatus status = getRandomItemStatus();

            Item item = new Item(name, "Description for " + name, category,
                    discountRate, status,
                    (int) (Math.random() * 100), date, promotionType, price, brand);

            ItemDetail itemDetail = new ItemDetail(item, getRandomDeliveryMethod(),
                    name + "을 위한 판매자 정보", getRandomProductInfo(),
                    getRandomPackagingType(), name + "을 위한 주의 사항",
                    (int) (Math.random() * 100), getRandomAdditionalDescription(),
                    Arrays.asList("Image URL for " + name),
                    getRandomPromotionImageUrl());  // 랜덤 프로모션 이미지 URL 추가

            item.setItemDetail(itemDetail);
            return item;
        }

        private void createReviewsForItem(Item item, int maxReviewCount) {
            int reviewCount = ThreadLocalRandom.current().nextInt(1, maxReviewCount + 1);

            for (int i = 0; i < reviewCount; i++) {
                // 랜덤 멤버 ID 생성 (1부터 50까지)
                long memberId = ThreadLocalRandom.current().nextLong(1, 51);

                // 멤버 엔티티 조회 (엔티티 매니저를 사용하여 ID에 해당하는 멤버 조회)
                Member member = em.find(Member.class, memberId);

                // 리뷰 생성 시 멤버 객체를 할당
                Review review = new Review(member, item, getRandomReviewComment(),
                        (int) (Math.random() * 5) + 1,
                        (int) (Math.random() * 100),
                        Arrays.asList("Review Image URL " + i),
                        getRandomReviewWriteDate());
                em.persist(review);
            }
        }

        private LocalDateTime getRandomReviewWriteDate() {
            long minDay = LocalDate.now().minusYears(1).toEpochDay();
            long maxDay = LocalDate.now().toEpochDay();
            long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);
            return LocalDate.ofEpochDay(randomDay).atStartOfDay()
                    .plusHours(ThreadLocalRandom.current().nextInt(24))
                    .plusMinutes(ThreadLocalRandom.current().nextInt(60));
        }

        private String getRandomReviewComment() {
            String[] comments = {
                    "정말 만족스러운 제품입니다!",
                    "가격 대비 훌륭해요.",
                    "배송이 빠르고 제품도 좋네요.",
                    "색상과 디자인이 마음에 들어요.",
                    "기대했던 것보다 훨씬 좋아요.",
                    "재구매 의사 있습니다.",
                    "사이즈가 딱 맞아요.",
                    "사용감이 좋습니다.",
                    "가성비 최고의 제품입니다."
            };

            return comments[ThreadLocalRandom.current().nextInt(comments.length)];
        }

        private LocalDate createRandomDate(int startYear, int endYear) {
            long minDay = LocalDate.of(startYear, 1, 1).toEpochDay();
            long maxDay = LocalDate.of(endYear, 1, 1).toEpochDay();
            long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);
            return LocalDate.ofEpochDay(randomDay);
        }

        private PromotionType getRandomPromotionType() {
            PromotionType[] promotionTypes = PromotionType.values();
            int randomIndex = ThreadLocalRandom.current().nextInt(promotionTypes.length);
            return promotionTypes[randomIndex];
        }

        private Integer getRandomPrice(int min, int max) {
            return ThreadLocalRandom.current().nextInt(min, max + 1);
        }

        private Integer getRandomDiscountRate(int min, int max) {
            return ThreadLocalRandom.current().nextInt(min, max + 1);
        }

        private String generateItemName(String category, int index) {
            String[] companyNames = {"삼성", "애플", "구글", "아마존", "나이키"};
            String[] productFeatures = {"블랙", "대형", "4K", "무선", "고급"};

            String companyName = companyNames[ThreadLocalRandom.current().nextInt(companyNames.length)];
            String productFeature = productFeatures[ThreadLocalRandom.current().nextInt(productFeatures.length)];
            String uniqueNumber = String.format("%03d", index);

            return companyName + " " + productFeature + " " + category + " " + uniqueNumber;
        }

        // 프로모션 이미지 URL을 랜덤으로 생성하는 메소드
        private String getRandomPromotionImageUrl() {
            String[] urlBases = {"http://example.com/promo/", "http://myshop.com/promo/", "http://sales.com/promo/"};
            int index = ThreadLocalRandom.current().nextInt(urlBases.length);
            int imgNumber = ThreadLocalRandom.current().nextInt(1, 101); // 1부터 100까지의 이미지 번호
            return urlBases[index] + "image" + imgNumber + ".jpg";
        }

        // 포장 타입을 랜덤으로 생성하는 메소드
        private String getRandomPackagingType() {
            String[] types = {"표준 박스", "친환경 포장", "선물 포장", "보안 포장"};
            return types[ThreadLocalRandom.current().nextInt(types.length)];
        }

        // 제품 정보를 랜덤으로 생성하는 메소드
        private String getRandomProductInfo() {
            String[] infos = {"고급 재료로 제작됨", "에너지 절약 모델", "2년 보증 포함", "사용자 친화적 인터페이스"};
            return infos[ThreadLocalRandom.current().nextInt(infos.length)];
        }

        // 배송 방식을 랜덤으로 생성하는 메소드
        private String getRandomDeliveryMethod() {
            String[] methods = {"무료 배송", "익스프레스 배송", "매장 픽업", "지역 배송"};
            return methods[ThreadLocalRandom.current().nextInt(methods.length)];
        }

        // 추가 설명을 랜덤으로 생성하는 메소드
        private String getRandomAdditionalDescription() {
            String[] descriptions = {
                    "이 제품은 한정판입니다",
                    "무료 액세서리가 포함되어 있습니다",
                    "하나 구매하면 하나 무료",
                    "해당 카테고리에서 베스트셀러"
            };
            return descriptions[ThreadLocalRandom.current().nextInt(descriptions.length)];
        }

        private String getRandomBrandName() {
            String[] brandNames = {"삼성", "애플", "구글", "아마존", "나이키", "아디다스", "LG", "소니", "샤오미"};
            return brandNames[ThreadLocalRandom.current().nextInt(brandNames.length)];
        }

        private ItemStatus getRandomItemStatus() {
            ItemStatus[] statuses = ItemStatus.values();
            return statuses[ThreadLocalRandom.current().nextInt(statuses.length)];
        }
    }
}
