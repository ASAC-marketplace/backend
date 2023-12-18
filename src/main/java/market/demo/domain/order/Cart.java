package market.demo.domain.order;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import market.demo.domain.item.Item;
import market.demo.domain.member.Member;
import market.demo.exception.InvalidEntitySetException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Cart {

    @Id @GeneratedValue
    @Column(name = "cart_id")
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "cart", cascade = ALL)
    private List<CartItem> cartItems = new ArrayList<>();

    private Long amount;
    private Long salesTotalAmount;
    private Long totalAmount;

    public void addCartItem(Item item) {
        CartItem cartItem = new CartItem(item);

        cartItems.add(cartItem);
        if (cartItem.getCart() != this) {cartItem.setCart(this);}

        recalculateCartAmounts();
    }

    public Cart(Member member){
        this.setMember(member);
        this.setAmount(0L);
        this.setSalesTotalAmount(0L);
        this.setTotalAmount(0L);
    }

    public void setMember(Member member) {
        this.member = member;
        if (member != null && member.getCart() != this) {
            member.setCart(this);
        }
    }

    public void recalculateCartAmounts() {
        long total = 0L;
        long totalSalesAmount = 0L;

        for (CartItem cartItem : cartItems) {
            Item item = cartItem.getItem();
            long itemTotalPrice = (long) item.getItemPrice() * cartItem.getQuantity();
            long itemSalesPrice = (long) (itemTotalPrice * (item.getDiscountRate() / 100.0));

            total += itemTotalPrice;
            totalSalesAmount += itemSalesPrice;
        }

        this.amount = total;
        this.salesTotalAmount = totalSalesAmount;
        this.totalAmount = total - totalSalesAmount;
    }

    public void cartItemValidate(Item item){
        this.cartItems.stream().filter(cartItem -> cartItem.getItem() == item).forEach(cartItem -> {
            throw new InvalidEntitySetException("이미 장바구니에 넣은 상품입니다.");
        });
    }

    public void changeCartByCartItem(@NotNull Item item, int i){
        this.amount = this.getAmount() + (long) item.getItemPrice() * i;
        this.salesTotalAmount = this.getSalesTotalAmount() + calDiscountPrice(item) * i;
        this.totalAmount = this.getAmount() - this.getSalesTotalAmount();
    }

    private long calDiscountPrice(@NotNull Item item){
        return (long) ((long) item.getItemPrice() * item.getDiscountRate() / 100.0);
    }

    public void changeCartByDeleteItem(CartItem cartItem){
        this.amount = this.getAmount() - cartItem.getTotalPrice();
        this.salesTotalAmount = this.getSalesTotalAmount() - calDiscountPrice(cartItem.getItem());
        this.totalAmount = this.getAmount() - this.getSalesTotalAmount();
    }

}
