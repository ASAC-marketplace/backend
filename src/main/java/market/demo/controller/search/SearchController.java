package market.demo.controller.search;

import lombok.RequiredArgsConstructor;
import market.demo.domain.search.ItemSearchCondition;
import market.demo.domain.search.SearchKeyword;
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

import static market.demo.domain.search.QSearchKeyword.searchKeyword;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    //14 급상승 검색어

    @GetMapping("/complexitem")
    public ItemSearchResponse searchItemComplex(SearchKeyword id, ItemSearchCondition condition, Pageable pageable) {

        // condition에서 검색어 가져와서 searchKeyword의 검색 빈도수 늘리기(호출 서비스)
        searchKeyword.frequencyPlus(id, 10);

        // SearchService에 구현
        // 가능하면 급상승 검색어를 뿌려주는 api까지
        return searchService.searchResponse(condition, pageable);
    }

/**
    @GetMapping("/aaron")
    public ItemSearchResponse searchAaron(Integer age) {


        return searchService.searchAaron(age);
    }
 **/

    @GetMapping("/counts")
    public CountsAndPriceRangeDto getCounts(ItemSearchCondition condition) {
        return searchService.getCountsAndPrice(condition);
    }

    @GetMapping("/autokeyword")
    public List<ItemAutoDto> findAutoKeyword(String keyword, int limit) {
        return searchService.findItemNamesByKeyword(keyword, limit);
    }
}
