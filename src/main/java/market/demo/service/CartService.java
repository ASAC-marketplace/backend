package market.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.demo.domain.item.Item;
import market.demo.domain.member.Member;
import market.demo.domain.order.Cart;
import market.demo.domain.order.CartItem;
import market.demo.dto.cart.CartDto;
import market.demo.exception.CartItemNotFoundException;
import market.demo.exception.CartNotFoundException;
import market.demo.exception.ItemNotFoundException;
import market.demo.exception.MemberNotFoundException;
import market.demo.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CartService {
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    private Member getMemberByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new MemberNotFoundException("사용자를 찾을 수 없습니다. 로그인 해주세요"));
    }

    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("아이템을 찾을 수 없습니다."));
    }

    private Cart getCartByMember(Member member){
        return cartRepository.findByMember(member)
                .orElseThrow(() -> new CartNotFoundException("장바구니를 찾을 수 없습니다."));
    }

    private CartItem getCartItemByCartAndItem(Cart cart, Item item){
        return cartItemRepository.findByCartAndItem(cart, item)
                .orElseThrow(() -> new CartItemNotFoundException("cartItem을 찾을 수 없습니다."));
    }

    private Cart getOrCreateCart(Member member) {
        return cartRepository.findByMember(member).orElseGet(() -> new Cart(member));
    }

    public void insertCart(String loginId, Long itemId) {
        Member member = getMemberByLoginId(loginId);
        Item item = getItemById(itemId);

        //해당 멤버에 장바구니 내역이 있는지 여부
        Cart cart = getOrCreateCart(member);
        cart.cartItemValidate(item);

        //장바구니 추가
        cart.addCartItem(item);
        cartRepository.save(cart);
    }

    //장바구니 확인
    public CartDto showCart(String loginId) {
        Member member = getMemberByLoginId(loginId);
        Cart cart = getCartByMember(member);

        return new CartDto(cart);
    }

    public void changeCartItem(String loginId, Long itemId, int i) {
        Member member =getMemberByLoginId(loginId);
        Cart cart = getCartByMember(member);
        Item item = getItemById(itemId);
        CartItem cartItem =getCartItemByCartAndItem(cart, item);

        //수량 확인
        item.validItemStockQuantityByCartItem(cartItem, i);

        // 아이템 수량 조정
        cartItem.changeCartItemByQuantity(item, i);
        cartItemRepository.save(cartItem);

        // 수량 반영하여 cart에 저장
        cart.changeCartByCartItem(item, i);
        cartRepository.save(cart);
    }

    public void deleteCart(String loginId, Long itemId) {
        Member member = getMemberByLoginId(loginId);
        Cart cart = getCartByMember(member);
        Item item = getItemById(itemId);
        CartItem cartItem = getCartItemByCartAndItem(cart, item);

        // 카트 아이템 삭제
        cart.changeCartByDeleteItem(cartItem);
        cartItemRepository.deleteById(cartItem.getId());
        cartRepository.save(cart);
    }

    public void clearCart(String loginId) {
        Member member = getMemberByLoginId(loginId);
        Cart cart = getOrCreateCart(member);

        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear();

        cart.setAmount(0L);
        cart.setSalesTotalAmount(0L);
        cart.setTotalAmount(0L);

        cartRepository.save(cart);
    }
}
