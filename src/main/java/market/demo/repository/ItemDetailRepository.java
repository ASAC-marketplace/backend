package market.demo.repository;

import market.demo.domain.item.Item;
import market.demo.domain.item.ItemDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemDetailRepository extends JpaRepository<ItemDetail, Long>{
    ItemDetail findByItem(Item item);
}
