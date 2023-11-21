package market.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Item {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;

    // 할인율, 프로모션 정보 등을 추가
//    private Double discountRate;
//    private LocalDateTime promotionStart;
//    private LocalDateTime promotionEnd;

    // 상품 상태 관리 (예: NEW, BESTSELLER 등)
//    @Enumerated(EnumType.STRING)
//    private ItemStatus status;


    @OneToMany(mappedBy = "item")
    private List<Review> reviews = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
