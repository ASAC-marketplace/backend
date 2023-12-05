package market.demo.repository;

import market.demo.domain.search.ItemSearchCondition;
import market.demo.dto.search.ItemSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepositoryCustom {
    Page<ItemSearchDto> searchPageComplex(ItemSearchCondition condition, Pageable pageable);
}

