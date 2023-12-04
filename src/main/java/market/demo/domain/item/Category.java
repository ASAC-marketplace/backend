package market.demo.domain.item;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
public class Category {
    @Id
    @GeneratedValue
    @Column(name = "category_id")
    private Long id;
    private String name;

    // parent -> null 대분류, 소분류는 parent 필드에 대분류 카테고리를 참조
    @ManyToOne(fetch = LAZY) // 부모 카테고리 참조
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent") // 자식 카테고리 리스트
    private List<Category> children = new ArrayList<>();

    @OneToMany(mappedBy = "category")
    private List<Item> items = new ArrayList<>();

    ///////////// 테스트 데이터용
    public Category(String name) {
        this.name = name;
    }

    public Category(String name, Category parent) {
        this.name = name;
        this.parent = parent;
    }

    public Category() {

    }
    ////////////
}
