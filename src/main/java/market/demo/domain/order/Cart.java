package market.demo.domain.order;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import market.demo.domain.item.Item;
import market.demo.domain.member.Member;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
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

    public void addCartItem(CartItem cartItem) {
        cartItems.add(cartItem);
        if (cartItem.getCart() != this) {
            cartItem.setCart(this);
        }
        recalculateCartAmounts();
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
            long itemTotalPrice = item.getItemPrice() * cartItem.getQuantity();
            long itemSalesPrice = (long) (itemTotalPrice * (1 - item.getDiscountRate() / 100.0));

            total += itemTotalPrice;
            totalSalesAmount += itemSalesPrice;
        }

        this.amount = total;
        this.salesTotalAmount = totalSalesAmount;
        this.totalAmount = total - totalSalesAmount;
    }
}
