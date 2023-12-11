package market.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.demo.domain.item.Item;
import market.demo.domain.member.Member;
import market.demo.domain.order.Cart;
import market.demo.domain.order.CartItem;
import market.demo.dto.cart.CartDto;
import market.demo.dto.cart.CartItemDto;
import market.demo.exception.ItemNotFoundException;
import market.demo.exception.MemberNotFoundException;
import market.demo.repository.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CartService {
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;


    public void insertCart(String loginId, Long itemId) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new MemberNotFoundException("사용자를 찾을 수 없습니다. 로그인 해주세요"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("아이템을 찾을 수 없습니다."));

        //해당 멤버에 장바구니 내역이 있는지 여부
        Optional<Cart> optionalCart = cartRepository.findByMember(member);
        Cart cart = new Cart();
        if(optionalCart.isEmpty()){
            cart.setMember(member);
            cart.setAmount(0L);
            cart.setSalesTotalAmount(0L);
            cart.setTotalAmount(0L);
        }
        else cart = optionalCart.get();

        List<CartItem> cartItemList = cart.getCartItems();
        for(CartItem cartItem: cartItemList){
            if(cartItem.getItem() == item) throw new IllegalArgumentException("이미 장바구니에 넣은 상품입니다.");
        }

        CartItem cartItem = new CartItem();
        cartItem.setItem(item);
        cartItem.setQuantity((int)1);
        cartItem.setTotalPrice(item.getItemPrice() * (long) cartItem.getQuantity());
        cartItem.setCart(cart);
        Long discountPrice = (long) (item.getItemPrice()* item.getDiscountRate() * 0.01);

        //장바구니 추가
        cart.addCartItem(cartItem);
        cart.setAmount(cart.getAmount() + item.getItemPrice());
        cart.setSalesTotalAmount(cart.getSalesTotalAmount() + discountPrice);
        cart.setTotalAmount(cart.getAmount() - cart.getSalesTotalAmount());

        cartRepository.save(cart);
    }

    //장바구니 확인
    public CartDto showCart(String loginId) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new MemberNotFoundException("사용자를 찾을 수 없습니다. 로그인 해주세요"));

        Cart cart = cartRepository.findByMember(member)
                .orElseThrow(() -> new IllegalArgumentException("장바구니를 찾을 수 없습니다."));

        List<CartItem> cartItemList = cart.getCartItems();
        List<CartItemDto> cartItemDtos = new ArrayList<>();

        for(CartItem cartItem: cartItemList){
            CartItemDto cartItemDto = getCartItemDto(cartItem);
            cartItemDtos.add(cartItemDto);
        }

        CartDto cartDto = new CartDto();
        cartDto.setCartId(cart.getId());
        cartDto.setCartItemDtos(cartItemDtos);
        cartDto.setAmount(cart.getAmount());
        cartDto.setSalesTotalAmount(cart.getSalesTotalAmount());
        cartDto.setTotalAmount(cart.getTotalAmount());

        return cartDto;
    }

    @NotNull
    private static CartItemDto getCartItemDto(CartItem cartItem) {
        Item item  = cartItem.getItem();
        CartItemDto cartItemDto = new CartItemDto();

        cartItemDto.setItemId(item.getId());
        cartItemDto.setItemName(item.getName());
        cartItemDto.setDiscountRate(item.getDiscountRate());
        cartItemDto.setItemPrice(item.getItemPrice());
        cartItemDto.setItemCount(cartItem.getQuantity());
        cartItemDto.setTotalPrice(item.getItemPrice() * cartItem.getQuantity());
        cartItemDto.setSalePrice((int) (cartItemDto.getTotalPrice()
                * (100 - item.getDiscountRate()) * 0.01));
        cartItemDto.setPromotionImageUrl(item.getItemDetail().getPromotionImageUrl());
        return cartItemDto;
    }

    public void changeCartItem(Long cartId, Long itemId, int i) {
        try {
            Cart cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new IllegalArgumentException("장바구니를 찾을 수 없습니다."));
            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new ItemNotFoundException("상품을 찾을 수 없습니다."));

            CartItem cartItem = cartItemRepository.findByCartAndItem(cart, item)
                    .orElseThrow(() -> new IllegalArgumentException("cartItem을 찾을 수 없습니다."));
            log.info(String.valueOf(cartItem.getId()));

            cartItem.setQuantity(cartItem.getQuantity() + i);

            if (item.getStockQuantity() - cartItem.getQuantity() < 0) {
                throw new IllegalArgumentException("해당 상품 재고가 부족합니다.");
            }

            cartItem.setTotalPrice((long) cartItem.getQuantity() * item.getItemPrice());
            cartItemRepository.save(cartItem);

            cart.setAmount(cart.getAmount() + (long) item.getItemPrice() * i);
            cart.setSalesTotalAmount((long) (cart.getSalesTotalAmount() +
                    item.getItemPrice() * (100 - item.getDiscountRate()) * 0.01 * i));
            cart.setTotalAmount(cart.getAmount() - cart.getSalesTotalAmount());
            cartRepository.save(cart);
        } catch (IllegalArgumentException | ItemNotFoundException e) {
            log.error("오류 발생: " + e.getMessage());
            throw e; // 예외를 다시 던져서 상위 수준에서 처리할 수 있도록 함
        } catch (Exception e) {
            log.error("서버 내부 오류: " + e.getMessage());
            throw new RuntimeException("서버 내부 오류 발생", e); // 예외를 다시 던져서 상위 수준에서 처리할 수 있도록 함
        }
    }


    /*public void changeCartItem(String loginId, Long cartId, Long itemId, int i) {

        try{
            Cart cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new IllegalArgumentException("장바구니를 찾을 수 없습니다."));
            Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new MemberNotFoundException("사용자를 찾을 수 없습니다. 로그인 해주세요"));
            Item item = itemRepository.findById(itemId)
                    .orElseThrow(()->new ItemNotFoundException("상품을 찾을 수 없습니다."));
            CartItem cartItem = cartItemRepository.findByCartAndItem(cart, item)
                    .orElseThrow(()-> new IllegalArgumentException("cartitem을 찾을 수 없음"));

            cartItem.setQuantity(cartItem.getQuantity()+i);
            if(item.getStockQuantity() - cartItem.getQuantity() < 0)
                throw new IllegalArgumentException("해당 상품 재고가 부족합니다.");
            cartItem.setTotalPrice((long)cartItem.getQuantity() * item.getItemPrice());

            cartItemRepository.save(cartItem);
            cart.setAmount(cart.getAmount() + (long) item.getItemPrice() * i);
            cart.setSalesTotalAmount((long) (cart.getSalesTotalAmount() +
                    item.getItemPrice() * (100 - item.getDiscountRate()) * 0.01 * i));
            cart.setTotalAmount(cart.getAmount() - cart.getSalesTotalAmount());
            cartRepository.save(cart);
        }catch (IllegalArgumentException | ItemNotFoundException e) {
            // 원하는 작업 수행: 로그 남기기, 사용자에게 메시지 전달 등
            log.error("오류 발생: " + e.getMessage());
            // e.printStackTrace(); // 스택 트레이스 출력 (선택 사항)
            // throw e; // 원하는 경우 예외 다시 던지기 (선택 사항)
        } catch (Exception e) {
            log.error("서버 내부 오류: " + e.getMessage());
            // e.printStackTrace(); // 스택 트레이스 출력 (선택 사항)
            // throw e; // 원하는 경우 예외 다시 던지기 (선택 사항)
        }

    }*/
}
