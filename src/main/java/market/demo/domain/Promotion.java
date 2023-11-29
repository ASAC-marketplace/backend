package market.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import market.demo.domain.status.PromotionType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Promotion {

    @Id
    @GeneratedValue
    @Column(name = "promotion_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private PromotionType type;

    private LocalDateTime promotionStart;
    private LocalDateTime promotionEnd;

    private String description;

    @ManyToMany(mappedBy = "promotions")
    private List<Item> items = new ArrayList<>(); //해당 프로모션에 포함된 상품 목록
}
