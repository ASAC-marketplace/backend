package market.demo.repository;

import market.demo.domain.search.ItemSearchCondition;
import market.demo.dto.category.CategoryDto;
import market.demo.dto.search.CountsAndPriceRangeDto;
import market.demo.dto.search.CountsAndPriceRangeNoCategoryDto;
import market.demo.dto.search.ItemAutoDto;
import market.demo.dto.search.ItemSearchResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepositoryCustom {
    ItemSearchResponse searchPageComplex(ItemSearchCondition condition, Pageable pageable);

    List<ItemAutoDto> findItemNamesByKeyword(String keyword, int limit);

    CountsAndPriceRangeDto getCounts(ItemSearchCondition condition);

    List<CategoryDto> findAllCategoriesWithSubcategories();

    CountsAndPriceRangeNoCategoryDto getCountsNoCategory(ItemSearchCondition condition);
}


