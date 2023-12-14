package market.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.demo.domain.etc.Delivery;
import market.demo.domain.member.Member;
import market.demo.domain.order.Cart;
import market.demo.domain.order.CartItem;
import market.demo.domain.order.Order;
import market.demo.domain.order.OrderItem;
import market.demo.domain.status.DeliveryStatus;
import market.demo.domain.status.OrderStatus;
import market.demo.dto.mypage.MyOrderDetailDto;
import market.demo.dto.mypage.MyOrderDto;
import market.demo.dto.order.OrderDto;
import market.demo.dto.order.OrderItemDto;
import market.demo.exception.MemberNotFoundException;
import market.demo.exception.OrderNotFoundException;
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
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;

    private Member getMemberByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new MemberNotFoundException("사용자를 찾을 수 없습니다. 로그인 해주세요"));
    }

    private Cart getCartByMember(Member member) {
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

    private Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("주문 내역이 없습니다."));
    }

    private List<Order> getUserOrderListByMonthAfter(Member member, int month) {
        LocalDateTime lastTime = LocalDateTime.now().minusMonths(month);
        return orderRepository.findAllByMemberAndOrderStatusAndOrderDateTimeAfter(member, OrderStatus.COMPLETED, lastTime)
                .orElseThrow(() -> new OrderNotFoundException("최근 " + month + "이내의 주문내역이 없습니다."));

    }

    public List<MyOrderDto> showUserOrders(String loginId, int month) {
        Member member = getMemberByLoginId(loginId);
        List<Order> orders = getUserOrderListByMonthAfter(member, month);

        return createMyOrderDtos(orders);
    }

    private @NotNull List<MyOrderDto> createMyOrderDtos(@NotNull List<Order> orders) {
        return orders.stream()
                .map(MyOrderDto::new)
                .collect(Collectors.toList());
    }

    public MyOrderDetailDto showUserOrderDetail(Long orderId) {
        Order order = getOrderById(orderId);
        return new MyOrderDetailDto(order);
    }

    public List<MyOrderDto> showUserOrders(String loginId, int month) {
        Member member = getMemberByLoginId(loginId);
        List<Order> orders = getUserOrderListByMonthAfter(member, month);

        return createMyOrderDtos(orders);
    }

    private @NotNull List<MyOrderDto> createMyOrderDtos(@NotNull List<Order> orders) {
        List<MyOrderDto> myOrderDtos = new ArrayList<>();

        for(Order order: orders){
            MyOrderDto myOrderDto = new MyOrderDto();

            myOrderDto.setOrderDateTime(order.getOrderDateTime());
            myOrderDto.setTotalAmount(order.getTotalAmount());
            myOrderDto.setOrderId(order.getId());
            myOrderDto.setPaymentMethod(order.getPayment().getPaymentMethod());
            myOrderDto.setDeliveryStatus(order.getDelivery().getDeliveryStatus());
            myOrderDto.setItemName(order.getOrderItems().get(0).getItem().getName());

            myOrderDtos.add(myOrderDto);
        }

        return myOrderDtos;
    }

    private @NotNull MyOrderDetailDto createOrderDetail(@NotNull Order order){
        MyOrderDetailDto myOrderDetailDto = new MyOrderDetailDto();
        myOrderDetailDto.setTotalAmount(order.getTotalAmount());
        myOrderDetailDto.setOrderId(order.getId());
        myOrderDetailDto.setPaymentMethod(order.getPayment().getPaymentMethod());
        myOrderDetailDto.setDeliveryStatus(order.getDelivery().getDeliveryStatus());
        myOrderDetailDto.setItemName(order.getOrderItems().get(0).getItem().getName());
        myOrderDetailDto.setOrderDateTime(order.getOrderDateTime());
        myOrderDetailDto.setOrderItemDtos(createOrderItemDto(order));

        return myOrderDetailDto;
    }

    public MyOrderDetailDto showUserOrderDetail(Long orderId) {
        Order order = getOrderById(orderId);
        return createOrderDetail(order);
    }
}
