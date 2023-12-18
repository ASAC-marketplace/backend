package market.demo.domain.etc;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import market.demo.domain.item.Item;
import market.demo.domain.member.Member;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
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

    public Wishlist(Member member){
        this.member = member;
    }

    public Wishlist() {

    }

    public boolean containsItem(Item item){
        return this.items.contains(item);
    }

    public void addItem(Item item) {
        if (!this.containsItem(item)) this.items.add(item);
        else throw new IllegalArgumentException("이미 찜한 상품입니다");
    }

    public void removeItem(Item item){
        if (this.containsItem(item)) this.items.remove(item);
        else throw new IllegalArgumentException("찜한 목록이 아닙니다.");
    }
}
