package market.demo.controller.search;

import lombok.RequiredArgsConstructor;
import market.demo.domain.search.ItemSearchCondition;
import market.demo.dto.search.CountsAndPriceRangeDto;
import market.demo.dto.search.ItemAutoDto;
import market.demo.dto.search.ItemSearchResponse;
import market.demo.service.SearchService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;


    @GetMapping("/complexitem")
    public ItemSearchResponse searchItemComplex(ItemSearchCondition condition, Pageable pageable) {
        return searchService.searchResponse(condition, pageable);
    }

    @GetMapping("/counts")
    public CountsAndPriceRangeDto getCounts(ItemSearchCondition condition) {
        return searchService.getCountsAndPrice(condition);
    }

    @GetMapping("/autokeyword")
    public List<ItemAutoDto> findAutoKeyword(String keyword, int limit) {
        return searchService.findItemNamesByKeyword(keyword, limit);
    }
}
