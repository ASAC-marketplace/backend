package market.demo.service;

import lombok.RequiredArgsConstructor;
import market.demo.domain.search.ItemSearchCondition;
import market.demo.dto.search.CountsAndPriceRangeDto;
import market.demo.dto.search.ItemAutoDto;
import market.demo.dto.search.ItemSearchDto;
import market.demo.dto.search.ItemSearchResponse;
import market.demo.repository.ItemRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final ItemRepositoryCustom itemRepositoryCustom;

   public ItemSearchResponse searchResponse(ItemSearchCondition condition, Pageable pageable) {
       return itemRepositoryCustom.searchPageComplex(condition, pageable);
   }

   public List<ItemAutoDto> findItemNamesByKeyword(String keyword, int limit) {
       return itemRepositoryCustom.findItemNamesByKeyword(keyword, limit);
   }

   public CountsAndPriceRangeDto getCountsAndPrice(ItemSearchCondition condition) {
       return itemRepositoryCustom.getCounts(condition);
   }
}
