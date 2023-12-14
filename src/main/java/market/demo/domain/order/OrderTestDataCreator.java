package market.demo.domain.order;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.demo.domain.etc.Delivery;
import market.demo.domain.item.Item;
import market.demo.domain.member.Address;
import market.demo.domain.member.Member;
import market.demo.domain.status.DeliveryStatus;
import market.demo.domain.status.OrderStatus;
import market.demo.service.CartService;
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
        initService.createTestData(200);
    }

    @Component
    @RequiredArgsConstructor
    @Slf4j
    static class InitService {
        @PersistenceContext
        private EntityManager em;

        private final CartService cartService;

        @Transactional
        public void createTestData(int numberOfOrders) {
            for (int i = 0; i < numberOfOrders; i++) {
                // 임의의 멤버 조회
                long memberId = i + 1;
                Member member = em.find(Member.class, memberId);

                // 장바구니에 아이템 추가
                Cart cart = member.getCart();
                int numberOfItems = ThreadLocalRandom.current().nextInt(1, 5); // 장바구니 아이템 개수
                for (int j = 0; j < numberOfItems; j++) {
                    long itemId = ThreadLocalRandom.current().nextLong(1, 51);
                    Item item = em.find(Item.class, itemId);
                    if (!isItemInCart(cart, item)) {
                        cartService.insertCart(member.getLoginId(), item.getId());
                    }
                }
                cart.recalculateCartAmounts();

                // 주문 생성
                OrderStatus[] orderStatusValues = OrderStatus.values();
                OrderStatus randomOrderStatus = orderStatusValues[ThreadLocalRandom.current().nextInt(orderStatusValues.length)];
                LocalDateTime orderDateTime = LocalDateTime.now().minusDays(ThreadLocalRandom.current().nextLong(1, 30));
                Order order = new Order(member, orderDateTime, randomOrderStatus);

                // 장바구니 아이템을 주문 아이템으로 전환
                cart.getCartItems().forEach(cartItem -> {
                    OrderItem orderItem = new OrderItem(cartItem.getItem(), cartItem.getQuantity(), cartItem.getItem().getItemPrice() * cartItem.getQuantity());
                    order.addOrderItem(orderItem);
                });

                // 주문에 총 금액 설정 및 저장
                order.setTotalAmount(cart.getTotalAmount());
                em.persist(order);

                // 배송 객체 생성 및 저장
                Address address = new Address("도시-" + i, "거리-" + i, "우편번호-" + i);
                Delivery delivery = new Delivery(order, address, DeliveryStatus.READY);
                order.setDelivery(delivery);
                em.persist(delivery);
            }
        }
    }
    private static boolean isItemInCart(Cart cart, Item item) {
        return cart.getCartItems().stream()
                .anyMatch(cartItem -> cartItem.getItem().equals(item));
    }
}