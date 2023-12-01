package market.demo.repository;

import market.demo.domain.item.Item;
import market.demo.domain.item.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByItem(Item item);

//    @Query("SELECT r FROM Review r WHERE r.item = :item")
//    List<Review> findAllByItem(Item item);
//
}
