package market.demo.domain.item;

import jakarta.persistence.*;
import lombok.Getter;
import market.demo.domain.status.ItemStatus;

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
    private Double discountRate;
    private LocalDateTime promotionStart;
    private LocalDateTime promotionEnd;

// 상품 상태 관리 (예: NEW, BESTSELLER 등)
    @Enumerated(EnumType.STRING)
    private ItemStatus status;

    @OneToMany(mappedBy = "item")
    private List<Review> reviews = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private String description;

    @OneToOne(mappedBy = "item", cascade = ALL, fetch = LAZY)
    private ItemDetail itemDetail;

    private Integer stockQuantity;

    @ManyToMany
    @JoinTable(
            name = "item_promotion",
            joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "promotion_id")
    )
    private List<Promotion> promotions = new ArrayList<>();
}
