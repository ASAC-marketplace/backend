package market.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.demo.domain.item.Item;
import market.demo.domain.member.Member;
import market.demo.domain.order.Order;
import market.demo.domain.order.OrderItem;
import market.demo.domain.status.OrderStatus;
import market.demo.dto.order.OrderItemDto;
import market.demo.dto.order.OrderListDto;
import market.demo.exception.ItemNotFoundException;
import market.demo.exception.MemberNotFoundException;
import market.demo.exception.OrderNotFoundException;
import market.demo.repository.ItemRepository;
import market.demo.repository.MemberRepository;
import market.demo.repository.OrderItemRepository;
import market.demo.repository.OrderRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderItemRepository orderItemRepository;


    public void addOrder(String loginId, Long itemId) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new MemberNotFoundException("사용자를 찾을 수 없습니다. 로그인 해주세요"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("아이템을 찾을 수 없습니다."));

        //해당 멤버에 장바구니 내역이 있는지 여부
        Optional<Order> optionalOrder = orderRepository.findByMemberAndOrderStatus(member, OrderStatus.PENDING);
        Order order = new Order();
        if(optionalOrder.isEmpty()){
            order.setMember(member);
            order.setOrderStatus(OrderStatus.PENDING);
            order.setTotalAmount((long) 0);
            order.setOrderDateTime(LocalDateTime.now());
        }else order = optionalOrder.get();

        List<OrderItem> orderItemList = order.getOrderItems();
        for(OrderItem orderItem: orderItemList){
            if(orderItem.getItem() == item) throw new IllegalArgumentException("이미 장바구니에 넣은 상품입니다.");
        }

        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderCount((int)1);
        orderItem.setOrderPrice(item.getItemPrice() * orderItem.getOrderCount());
        orderItem.setOrder(order);

        order.addOrderItem(orderItem);
        order.setTotalAmount(order.getTotalAmount() + item.getItemPrice());

        orderRepository.save(order);
    }

    //장바구니 확인
    public OrderListDto showOrderList(String loginId) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new MemberNotFoundException("사용자를 찾을 수 없습니다. 로그인 해주세요"));

        Order order = orderRepository
                .findByMemberAndOrderStatus(member, OrderStatus.PENDING)
                .orElseThrow(() -> new OrderNotFoundException("장바구니를 찾을 수 없습니다."));

        List<OrderItem> orderItemList = order.getOrderItems();
        List<OrderItemDto> orderItemDtoList = new ArrayList<>();

        Long totalAmount = 0L;
        for(OrderItem orderItem: orderItemList){
            OrderItemDto orderItemDto = getOrderItemDto(orderItem);
            totalAmount += orderItemDto.getSaleItemPrice();

            orderItemDtoList.add(orderItemDto);
        }

        OrderListDto orderListDto = new OrderListDto();
        orderListDto.setOrderId(order.getId());
        orderListDto.setOrderItemDtos(orderItemDtoList);
        orderListDto.setAmount(order.getTotalAmount());
        orderListDto.setTotalAmount(totalAmount);
        orderListDto.setSalesTotalAmount(orderListDto.getAmount() - totalAmount);

        return orderListDto;
    }

    @NotNull
    private static OrderItemDto getOrderItemDto(OrderItem orderItem) {
        Item item  = orderItem.getItem();
        OrderItemDto orderItemDto = new OrderItemDto();

        orderItemDto.setItemId(item.getId());
        orderItemDto.setItemName(item.getName());
        orderItemDto.setDiscountRate(item.getDiscountRate());
        orderItemDto.setItemPrice(item.getItemPrice());
        orderItemDto.setPromotionImageUrl(item.getItemDetail().getPromotionImageUrl());
        orderItemDto.setItemTotalPrice(orderItem.getOrderPrice());
        orderItemDto.setItemCount(orderItem.getOrderCount());
        orderItemDto.setSaleItemPrice((int) (orderItemDto.getItemTotalPrice()* (100 - item.getDiscountRate()) * 0.01));
        return orderItemDto;
    }

    public List<OrderItemDto> getUserOrderList(String loginId) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new MemberNotFoundException("사용자를 찾을 수 없습니다. 로그인 해주세요"));

        List<Order> orders = orderRepository.findAllByMemberAndOrderStatus(member, OrderStatus.COMPLETED)
                .orElseThrow(() -> new OrderNotFoundException("주문내역이 없습니다."));


        List<OrderItemDto> orderItemDtos = new ArrayList<>();


        return  orderItemDtos;
    }

    public void addOrderItem(String loginId, Long orderId, Long itemId) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new MemberNotFoundException("사용자를 찾을 수 없습니다. 로그인 해주세요"));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("장바구니를 찾을 수 없습니다."));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(()->new ItemNotFoundException("상품을 찾을 수 없습니다."));

        OrderItem orderItem = orderItemRepository.findByOrderAndItem(order, item);

        Long prePrice = (long) orderItem.getOrderPrice();
        orderItem.setOrderCount(orderItem.getOrderCount()+1);
        orderItem.setOrderPrice(orderItem.getOrderPrice()+item.getItemPrice());

        orderItemRepository.save(orderItem);
        order.setTotalAmount(order.getTotalAmount() - prePrice + orderItem.getOrderPrice());
        orderRepository.save(order);




    }
}
