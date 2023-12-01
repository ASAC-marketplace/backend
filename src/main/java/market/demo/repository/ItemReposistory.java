package market.demo.repository;

import market.demo.domain.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemReposistory extends JpaRepository<Item, Long> {
}
