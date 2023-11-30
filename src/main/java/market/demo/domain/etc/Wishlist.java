package market.demo.domain.etc;

import jakarta.persistence.*;
import lombok.Getter;
import market.demo.domain.item.Item;
import market.demo.domain.member.Member;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Wishlist {

    @Id
    @GeneratedValue
    @Column(name = "wishlist_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToMany
    @JoinTable(
            name = "wishlist_items",
            joinColumns = @JoinColumn(name = "wishlist_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Item> items = new ArrayList<>();
}
