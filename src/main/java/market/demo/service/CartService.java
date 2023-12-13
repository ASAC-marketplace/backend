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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

    private Cart getCartById(Long cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니를 찾을 수 없습니다."));
    }

    private Cart getCartByMember(Member member){
        return cartRepository.findByMember(member)
                .orElseThrow(() -> new IllegalArgumentException("장바구니를 찾을 수 없습니다."));
    }

    private CartItem getCartItemByCartAndItem(Cart cart, Item item){
        return cartItemRepository.findByCartAndItem(cart, item)
                .orElseThrow(() -> new IllegalArgumentException("cartItem을 찾을 수 없습니다."));
    }


    private Cart getOrCreateCart(Member member) {
        return cartRepository.findByMember(member).orElseGet(() -> createNewCart(member));
    }
    @NotNull
    private  Cart createNewCart(Member member){
        Cart cart = new Cart();
        cart.setMember(member);
        cart.setAmount(0L);
        cart.setSalesTotalAmount(0L);
        cart.setTotalAmount(0L);

        return cart;
    }
    @NotNull
    private CartItem insertNewItemAndCart(Item item, Cart cart){
        CartItem cartItem = new CartItem();
        cartItem.setItem(item);
        cartItem.setQuantity((int)1);
        cartItem.setTotalPrice(item.getItemPrice() * (long) cartItem.getQuantity());
        cartItem.setCart(cart);

        return cartItem;
    }

    private long calDiscountPriceByItem(@NotNull Item item){
        return (long) (item.getItemPrice()* item.getDiscountRate() * 0.01);
    }


    private static long calDiscountPercentage(@NotNull Item item){
        return (long) ((100 - item.getDiscountRate()) * 0.01);
    }


    private void addCartItemIntoCart(@NotNull Cart cart, CartItem cartItem, @NotNull Item item){
        cart.addCartItem(cartItem);
        cart.setAmount(cart.getAmount() + item.getItemPrice());
        cart.setSalesTotalAmount(cart.getSalesTotalAmount() + calDiscountPriceByItem(item));
        cart.setTotalAmount(cart.getAmount() - cart.getSalesTotalAmount());

        cartRepository.save(cart);
    }

    public void insertCart(String loginId, Long itemId) {
        Member member = getMemberByLoginId(loginId);
        Item item = getItemById(itemId);
        Cart cart = getOrCreateCart(member); //해당 멤버에 장바구니 내역이 있는지 여부

        List<CartItem> cartItemList = cart.getCartItems();
        cartItemList.stream().filter(cartItem -> cartItem.getItem() == item).forEach(cartItem -> {
            throw new IllegalArgumentException("이미 장바구니에 넣은 상품입니다.");
        });
        CartItem cartItem = insertNewItemAndCart(item, cart);

        addCartItemIntoCart(cart, cartItem, item);//장바구니 추가
    }

    private List<CartItemDto> createNewCartItemDtosByCartItems(@NotNull List<CartItem> cartItems){
        return cartItems.stream().map(CartService::getCartItemDto).collect(Collectors.toList());
    }

    private @NotNull CartDto createNewCartDtoByCart(@NotNull Cart cart, List<CartItemDto> cartItemDtos){
        CartDto cartDto = new CartDto();
        cartDto.setCartId(cart.getId());
        cartDto.setCartItemDtos(cartItemDtos);
        cartDto.setAmount(cart.getAmount());
        cartDto.setSalesTotalAmount(cart.getSalesTotalAmount());
        cartDto.setTotalAmount(cart.getTotalAmount());

        return cartDto;
    }

    //장바구니 확인
    public CartDto showCart(String loginId) {
        Member member = getMemberByLoginId(loginId);
        Cart cart = getCartByMember(member);

        return createNewCartDtoByCart(cart, createNewCartItemDtosByCartItems(cart.getCartItems()));
    }

    private static @NotNull CartItemDto getCartItemDto(@NotNull CartItem cartItem) {
        Item item  = cartItem.getItem();
        CartItemDto cartItemDto = new CartItemDto();

        cartItemDto.setItemId(item.getId());
        cartItemDto.setItemName(item.getName());
        cartItemDto.setDiscountRate(item.getDiscountRate());
        cartItemDto.setItemPrice(item.getItemPrice());
        cartItemDto.setItemCount(cartItem.getQuantity());
        cartItemDto.setTotalPrice(item.getItemPrice() * cartItem.getQuantity());
        cartItemDto.setSalePrice((int) (cartItemDto.getTotalPrice() * calDiscountPercentage(item)));
        cartItemDto.setPromotionImageUrl(item.getItemDetail().getPromotionImageUrl());
        return cartItemDto;
    }

    private void saveCartItemByQuantity(@NotNull CartItem cartItem, @NotNull Item item, int i){
        cartItem.setQuantity(cartItem.getQuantity() + i);
        cartItem.setTotalPrice((long) cartItem.getQuantity() * item.getItemPrice());
        cartItemRepository.save(cartItem);
    }

    private void saveCartByCarItemChanges(@NotNull Cart cart, @NotNull Item item, int i ){
        cart.setAmount(cart.getAmount() + (long) item.getItemPrice() * i);
        cart.setSalesTotalAmount((long) (cart.getSalesTotalAmount() + item.getItemPrice() * calDiscountPercentage(item) * i));
        cart.setTotalAmount(cart.getAmount() - cart.getSalesTotalAmount());
        cartRepository.save(cart);
    }

    public void changeCartItem(Long cartId, Long itemId, int i) {
        Cart cart = getCartById(cartId);
        Item item = getItemById(itemId);
        CartItem cartItem =getCartItemByCartAndItem(cart, item);

        if(item.getStockQuantity() < cartItem.getQuantity() + i) throw new IllegalArgumentException("해당 상품 재고가 부족합니다.");
        if(cartItem.getQuantity() + i < 1 ) throw new IllegalArgumentException("0개 이하는 주문하실 수 없습니다.");

        saveCartItemByQuantity(cartItem, item, i); // 아이템 수량 조정
        saveCartByCarItemChanges(cart, item, i); // 수량 반영하여 cart에 저장
    }

    private void saveCartByDeleteItem(@NotNull Cart cart, @NotNull CartItem cartItem, Item item){
        cart.setAmount(cart.getAmount() - cartItem.getTotalPrice());
        cart.setSalesTotalAmount(cart.getSalesTotalAmount() - calDiscountPriceByItem(item) * cartItem.getQuantity());
        cart.setTotalAmount(cart.getAmount() - cart.getSalesTotalAmount());

        cartRepository.save(cart);
    }

    public void deleteCart(String loginId, Long itemId) {
        Member member = getMemberByLoginId(loginId);
        Cart cart = getCartByMember(member);
        Item item = getItemById(itemId);
        CartItem cartItem = getCartItemByCartAndItem(cart, item);

        saveCartByDeleteItem(cart, cartItem, item);
        cartItemRepository.deleteById(cartItem.getId()); // 카트 아이템 삭제
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
