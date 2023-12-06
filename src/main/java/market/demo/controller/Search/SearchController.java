package market.demo.controller.Search;

import lombok.RequiredArgsConstructor;
import market.demo.domain.search.ItemSearchCondition;
import market.demo.dto.search.ItemSearchDto;
import market.demo.dto.search.ItemSearchResponse;
import market.demo.service.SearchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/complexitem")
    public ItemSearchResponse searchItemComplex(ItemSearchCondition condition, Pageable pageable) {
        return searchService.searchResponse(condition, pageable);
    }
}
