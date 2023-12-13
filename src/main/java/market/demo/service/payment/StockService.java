package market.demo.service.payment;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.demo.domain.item.Item;
import market.demo.domain.order.Order;
import market.demo.domain.order.OrderItem;
import market.demo.exception.InvalidOrderException;
import market.demo.repository.ItemRepository;
import market.demo.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StockService {

    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;

    public void reduceStock(Long orderId) throws InvalidOrderException {
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new InvalidOrderException("Invalid Order ID: " + orderId));

        for (OrderItem orderItem : order.getOrderItems()) {
            Item item = orderItem.getItem();
            int orderedQuantity = orderItem.getOrderCount();
            log.info("Reducing stock of item {} by {}", item.getName(), orderedQuantity);
            item.decreaseStock(orderedQuantity);
        }
    }
}
