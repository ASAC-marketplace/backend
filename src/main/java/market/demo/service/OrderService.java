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
import market.demo.exception.InvalidDataException;
import market.demo.repository.ItemRepository;
import market.demo.repository.MemberRepository;
import market.demo.repository.OrderItemRepository;
import market.demo.repository.OrderRepository;
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
        return orderRepository.findByMemberAndOrderStatus(member, OrderStatus.PENDING).orElseGet(() -> new Order(member, cart));
    }

    public OrderDto showOrCreateOrder(String loginId) {
        Member member = getMemberByLoginId(loginId);
        Cart cart = getCartByMember(member);
        Order order = getOrCreateOrder(member, cart);

        orderItemRepository.deleteAll(order.getOrderItems());

        order.setOrderItems(cart, order);
        orderRepository.save(order);

        return new OrderDto(order, member, cart);
    }
}
