package market.demo.domain.item;

import jakarta.persistence.*;
import lombok.Getter;
import market.demo.domain.status.ItemStatus;
import market.demo.domain.type.PromotionType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.*;

@Entity
@Getter
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
    private Double itemPrice;

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

    private Integer price;

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

    public Item(String name, String description, Category category, Integer discountRate, ItemStatus status, Integer stockQuantity, LocalDate registerdDate, PromotionType promotionType, Integer price) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.discountRate = discountRate;
        this.status = status;
        this.stockQuantity = stockQuantity;
        this.registerdDate = registerdDate;
        this.promotionType = promotionType;
        this.price = price;
    }
    public Item() {

    }

    public void setItemDetail(ItemDetail itemDetail) {
        this.itemDetail = itemDetail;
    }
    /////////////////
}
