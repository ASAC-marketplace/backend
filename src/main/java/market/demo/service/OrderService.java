package market.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.demo.domain.etc.Delivery;
import market.demo.domain.item.Item;
import market.demo.domain.member.Member;
import market.demo.domain.order.Cart;
import market.demo.domain.order.CartItem;
import market.demo.domain.order.Order;
import market.demo.domain.order.OrderItem;
import market.demo.domain.status.DeliveryStatus;
import market.demo.domain.status.OrderStatus;
import market.demo.dto.order.OrderDto;
import market.demo.dto.order.OrderItemDto;
import market.demo.exception.MemberNotFoundException;
import market.demo.repository.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;

    private Member getMemberByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new MemberNotFoundException("사용자를 찾을 수 없습니다. 로그인 해주세요"));
    }

    private Cart getCartByMember(Member member){
        return cartRepository.findByMember(member)
                .orElseThrow(() -> new IllegalArgumentException("장바구니를 찾을 수 없습니다."));
    }

    private Order getOrCreateOrder(Member member, Cart cart) {
        return orderRepository.findByMemberAndOrderStatus(member, OrderStatus.PENDING).orElseGet(() -> createNewOrder(member, cart));
    }

    private @NotNull Delivery createNewDelivery(@NotNull Member member){
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());
        delivery.setDeliveryStatus(DeliveryStatus.PENDING);
        return delivery;
    }
    @NotNull
    private Order createNewOrder(Member member, @NotNull Cart cart){
        Order order = new Order();
        order.setMember(member);
        order.setTotalAmount(cart.getTotalAmount());
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setDelivery(createNewDelivery(member));
        orderRepository.save(order);
        return order;
    }

    private @NotNull List<OrderItem> createOrderItemList(@NotNull Cart cart, Order order){
        List<CartItem> cartItems = cart.getCartItems();
        List<OrderItem> orderItems = new ArrayList<>();

        cartItems.forEach(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(cartItem.getItem());
            orderItem.setOrderPrice(Math.toIntExact(cartItem.getTotalPrice()));
            orderItem.setOrderCount(cartItem.getQuantity());
            orderItem.setOrder(order);
            orderItems.add(orderItem);
        });
        return orderItems;
    }

    private @NotNull OrderDto createOrderDto(@NotNull Order order, @NotNull Member member, @NotNull Cart cart){
        OrderDto orderDto = new OrderDto();
        orderDto.setOrderId(order.getId());
        orderDto.setMemberName(member.getMemberName());
        orderDto.setAmount(cart.getAmount());
        orderDto.setAddress(order.getDelivery().getAddress());
        orderDto.setPhoneNumber(member.getPhoneNumber());
        orderDto.setSalesTotalAmount(cart.getSalesTotalAmount());
        orderDto.setTotalAmount(cart.getTotalAmount());
        List<OrderItemDto> orderItemDtos = new ArrayList<>();

        order.getOrderItems().forEach(orderItem -> {
            OrderItemDto orderItemDto = new OrderItemDto();
            orderItemDto.setItemCount(orderItem.getOrderCount());
            orderItemDto.setItemId(orderItem.getItem().getId());
            orderItemDto.setItemName(orderItem.getItem().getName());
            orderItemDto.setItemPrice(orderItem.getItem().getItemPrice());
            orderItemDto.setDiscountRate(orderItem.getItem().getDiscountRate());
            orderItemDtos.add(orderItemDto);
        });

        orderDto.setOrderItemDtos(orderItemDtos);
        return orderDto;
    }

    private void checkOrderAndCart(Order order, @NotNull Cart cart){
        for(CartItem cartItem : cart.getCartItems()){
            for(OrderItem orderItem : order.getOrderItems()){
                if (Objects.equals(cartItem.getItem(), orderItem.getItem())) {
                    orderItem.setOrderCount(cartItem.getQuantity());
                    orderItem.setOrderPrice(Math.toIntExact(cartItem.getTotalPrice()));
                    break;
                }
            }
        }
    }

    public OrderDto showOrCreateOrder(String loginId) {
        Member member = getMemberByLoginId(loginId);
        Cart cart = getCartByMember(member);
        Order order = getOrCreateOrder(member, cart);
        orderItemRepository.deleteAll(order.getOrderItems());
        order.setOrderItems(createOrderItemList(cart, order));
        orderRepository.save(order);
        return createOrderDto(order, member, cart);
    }
}
