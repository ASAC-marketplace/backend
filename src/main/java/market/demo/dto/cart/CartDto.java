package market.demo.dto.cart;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import market.demo.domain.order.Cart;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CartDto {
    private Long cartId;
    private Long amount; // 전체 금액
    private Long salesTotalAmount; // 할인 금액
    private Long totalAmount;// 할인 된 총 금액
    private List<CartItemDto> cartItemDtos = new ArrayList();

    public CartDto(Cart cart){
        this.cartId = cart.getId();
        this.amount = cart.getAmount();
        this.salesTotalAmount = cart.getSalesTotalAmount();
        this.totalAmount = cart.getTotalAmount();
        this.cartItemDtos = createCartItemDtosByCart(cart);
    }

    private List<CartItemDto> createCartItemDtosByCart(Cart cart){
        return cart.getCartItems().stream().map(CartItemDto::new).collect(Collectors.toList());
    }

}
