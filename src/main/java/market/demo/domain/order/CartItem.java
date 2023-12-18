package market.demo.domain.order;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import market.demo.domain.item.Item;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue
    @Column(name = "cart_item_id")
    private Long id;

    private int quantity;
    private Long totalPrice;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    public CartItem(Item item){
        this.quantity = 1;
        this.item = item;
        this.totalPrice = item.getItemPrice() * (long) this.quantity;
    }

    public void changeCartItemByQuantity(Item item, int i){
        this.quantity = this.getQuantity() + i;
        this.totalPrice = item.getItemPrice() * (long) this.quantity;
    }
}
