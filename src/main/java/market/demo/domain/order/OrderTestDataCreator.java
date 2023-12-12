package market.demo.domain.order;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import market.demo.domain.etc.Delivery;
import market.demo.domain.item.Item;
import market.demo.domain.member.Address;
import market.demo.domain.member.Member;
import market.demo.domain.status.DeliveryStatus;
import market.demo.domain.status.OrderStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class OrderTestDataCreator {

    private final InitService initService;

    public void init() {
        initService.createTestData(100);
    }

    @Component
    static class InitService {
        @PersistenceContext
        private EntityManager em;

        @Transactional
        public void createTestData(int numberOfOrders) {
            for (int i = 0; i < numberOfOrders; i++) {
                OrderStatus[] orderStatusValues = OrderStatus.values();
                OrderStatus randomOrderStatus = orderStatusValues[ThreadLocalRandom.current().nextInt(orderStatusValues.length)];

                // 임의의 멤버 조회
                long memberId = ThreadLocalRandom.current().nextLong(1, 51);
                Member member = em.find(Member.class, memberId);

                // 주문 생성
                LocalDateTime orderDateTime = LocalDateTime.now().minusDays(ThreadLocalRandom.current().nextLong(1, 30));
                Order order = new Order(member, orderDateTime, randomOrderStatus);

                // 여러 개의 주문 항목 생성 및 주문에 추가
                int numberOfItems = ThreadLocalRandom.current().nextInt(1, 5); // 주문 항목 개수
                List<OrderItem> orderItems = IntStream.range(0, numberOfItems).mapToObj(index -> {
                    long itemId = ThreadLocalRandom.current().nextLong(1, 51);
                    Item item = em.find(Item.class, itemId);
                    int orderCount = ThreadLocalRandom.current().nextInt(1, 5);
                    int orderPrice = item.getItemPrice() * orderCount;
                    return new OrderItem(item, orderCount, orderPrice);
                }).collect(Collectors.toList());

                orderItems.forEach(order::addOrderItem); // 주문 항목을 주문에 추가
                order.calculateTotalAmount(); // 총 금액 계산
                em.persist(order);

                // 배송 객체 생성 및 저장
                Address address = new Address("도시-" + i, "거리-" + i, "우편번호-" + i);
                Delivery delivery = new Delivery(order, address, DeliveryStatus.READY);
                order.setDelivery(delivery);
                em.persist(delivery);
            }
        }
    }
}