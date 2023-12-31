package market.demo.domain.item;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import market.demo.domain.order.CartItem;
import market.demo.domain.status.ItemStatus;
import market.demo.domain.type.PromotionType;
import market.demo.exception.InvalidOrderException;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@Setter
@Slf4j
public class Item {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;

    // 할인율, 프로모션 정보 등을 추가
    private Integer discountRate;
    private LocalDateTime promotionStart;
    private LocalDateTime promotionEnd;

//    private Integer itemPrice;

    // 상품 상태 관리 (예: NEW, BESTSELLER 등)
    @Enumerated(EnumType.STRING)
    private ItemStatus status;

    private LocalDate registerdDate;

    @OneToMany(mappedBy = "item")
    private List<Review> reviews = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private String description;

    @OneToOne(mappedBy = "item", cascade = ALL, fetch = LAZY)
    private ItemDetail itemDetail;

    private Integer stockQuantity;

    private Integer itemPrice;

    private String brand;

    @Enumerated(EnumType.STRING)
    private PromotionType promotionType = PromotionType.NONE; // 기본값으로 'NONE' 설정


    ///////////////// 테스트 데이터용
    public Item(String name, String description, Category category, Integer discountRate, ItemStatus status, Integer stockQuantity, LocalDate registerdDate) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.discountRate = discountRate;
        this.status = status;
        this.stockQuantity = stockQuantity;
        this.registerdDate = registerdDate;
    }

    public Item(String name, String description, Category category, Integer discountRate, ItemStatus status, Integer stockQuantity, LocalDate registerdDate, PromotionType promotionType, Integer itemPrice, String brand) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.discountRate = discountRate;
        this.status = status;
        this.stockQuantity = stockQuantity;
        this.registerdDate = registerdDate;
        this.promotionType = promotionType;
        this.itemPrice = itemPrice;
        this.brand = brand;
    }

    public Item() {

    }

    public void setItemDetail(ItemDetail itemDetail) {
        this.itemDetail = itemDetail;
    }
    /////////////////

    public void decreaseStock(int quantity) throws InvalidOrderException {
        if (this.stockQuantity < quantity) {
            throw new InvalidOrderException("아이템의 재고가 부족합니다. Item ID: " + this.getId());
        }
        log.info("stock = " + quantity);
        this.stockQuantity -= quantity;
    }

    public void validItemStockQuantityByCartItem(@NotNull CartItem cartItem, int i){
        if(this.stockQuantity < cartItem.getQuantity() + i)
            throw new IllegalArgumentException("해당 상품 재고가 부족합니다.");
        if(cartItem.getQuantity() + i < 1)
            throw new IllegalArgumentException("0개 이하는 주문하실 수 없습니다.");
    }
}

