package market.demo.controller.search;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.demo.domain.member.jwt.TokenProvider;
import market.demo.domain.search.ItemSearchCondition;
import market.demo.dto.search.*;
import market.demo.repository.ItemRepositoryCustom;
import market.demo.repository.ItemRepositoryImpl;
import market.demo.service.SearchService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;
    private final ItemRepositoryCustom itemRepositoryCustom;
    private final TokenProvider tokenProvider;

    @GetMapping("/complexitem")
    public ItemSearchResponse searchItemComplex(ItemSearchCondition condition, Pageable pageable) {
        if (condition.getName() != null) {
            searchService.recodeSearchKeyword(condition.getName());
        }
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

    @GetMapping("/counts-nocategory")
    public CountsAndPriceRangeNoCategoryDto getCountsNoCateogry(ItemSearchCondition condition) {
        return itemRepositoryCustom.getCountsNoCategory(condition);
    }

    @GetMapping("/autokeyword")
    public List<ItemAutoDto> findAutoKeyword(String keyword, int limit) {
        return searchService.findItemNamesByKeyword(keyword, limit);
    }

    //13번 추천 검색어
    @GetMapping("/recommend-keyword")
    public ResponseEntity<List<ItemRecommendDto>> getRecommendKeyword() {
        String loginId = tokenProvider.getLoginIdFromCurrentRequest();
        log.info("ID: " + loginId);
        return ResponseEntity.ok(searchService.getRecommendKeyword(loginId));
    }
}
