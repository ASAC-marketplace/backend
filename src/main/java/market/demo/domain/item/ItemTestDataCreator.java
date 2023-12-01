package market.demo.domain.item;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import market.demo.domain.status.ItemStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class ItemTestDataCreator {
    private final InitService initService;

    public ItemTestDataCreator(InitService initService) {
        this.initService = initService;
    }

    @PostConstruct
    public void init() {
        initService.createTestData(100);
    }

    @Component
    static class InitService {

        @PersistenceContext
        private EntityManager em;

        @Transactional
        public void createTestData(int numberOfItems) {
            Category parentCategory = new Category("대분류 카테고리");
            em.persist(parentCategory);

            for (int i = 0; i < numberOfItems; i++) {
                Category subCategory = new Category("소분류 카테고리 " + i, parentCategory);
                em.persist(subCategory);
                parentCategory.getChildren().add(subCategory);

                LocalDate randomDate = createRandomDate(2021, 2023);
                Item item = new Item("Item " + i, "Description for Item " + i, subCategory,
                        Math.random() * 0.2, ItemStatus.NEW,
                        (int) (Math.random() * 100), randomDate);

                ItemDetail itemDetail = new ItemDetail(item, "Delivery Method " + i,
                        "Seller Info " + i, "Product Info " + i,
                        "Packaging Type " + i, "Notes " + i,
                        (int) (Math.random() * 100),
                        "Additional Description " + i,
                        Arrays.asList("Image URL " + i));

                item.setItemDetail(itemDetail);
                em.persist(item);

                for (int j = 0; j < 5; j++) {
                    Review review = new Review(item, "Review Comment " + j + " for Item " + i,
                            (int) (Math.random() * 5) + 1,
                            (int) (Math.random() * 100),
                            Arrays.asList("Review Image URL " + j));
                    em.persist(review);
                }
            }
        }

        private LocalDate createRandomDate(int startYear, int endYear) {
            long minDay = LocalDate.of(startYear, 1, 1).toEpochDay();
            long maxDay = LocalDate.of(endYear, 1, 1).toEpochDay();
            long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);
            return LocalDate.ofEpochDay(randomDay);
        }
    }
}