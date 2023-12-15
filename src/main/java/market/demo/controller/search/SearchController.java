package market.demo.controller.search;

import lombok.RequiredArgsConstructor;
import market.demo.domain.search.ItemSearchCondition;
import market.demo.dto.search.CountsAndPriceRangeDto;
import market.demo.dto.search.ItemAutoDto;
import market.demo.dto.search.ItemRecommendDto;
import market.demo.dto.search.ItemSearchResponse;
import market.demo.service.SearchService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/complexitem")
    public ItemSearchResponse searchItemComplex(ItemSearchCondition condition, Pageable pageable) {
        searchService.recodeSearchKeyword(condition.getName());
        return searchService.searchResponse(condition, pageable);
    }

    @GetMapping("/top-keywords")
    public ResponseEntity<List<String>> getTopKeywordsFromNow() {
        List<String> topKeywords = searchService.getTopSearchKeywordsFromNow();
        return ResponseEntity.ok(topKeywords);
    }

    @GetMapping("/counts")
    public CountsAndPriceRangeDto getCounts(ItemSearchCondition condition) {
        return searchService.getCountsAndPrice(condition);
    }

    @GetMapping("/autokeyword")
    public List<ItemAutoDto> findAutoKeyword(String keyword, int limit) {
        return searchService.findItemNamesByKeyword(keyword, limit);
    }

    //13번 추천 검색어
    @GetMapping("/recommend-keyword")
    public ResponseEntity<List<ItemRecommendDto>> getRecommendKeyowrd(@RequestParam String loginId){
        return ResponseEntity.ok(searchService.getRecommendKeyword(loginId));
    }
}
