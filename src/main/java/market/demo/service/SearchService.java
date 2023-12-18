package market.demo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import market.demo.domain.search.ItemSearchCondition;
import market.demo.dto.search.CountsAndPriceRangeDto;
import market.demo.dto.search.ItemAutoDto;
import market.demo.domain.search.SearchHistory;
import market.demo.dto.search.ItemSearchResponse;
import market.demo.repository.ItemRepositoryCustom;
import market.demo.repository.SearchReposirory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SearchService {
    private final ItemRepositoryCustom itemRepositoryCustom;
    private final SearchReposirory searchReposirory;

    private static final Map<String, Integer> searchKeywordCache = new ConcurrentHashMap<>();

    public ItemSearchResponse searchResponse(ItemSearchCondition condition, Pageable pageable) {
        return itemRepositoryCustom.searchPageComplex(condition, pageable);
    }

    @CachePut(value = "searchKeywords", key = "#keyword")
    public void recodeSearchKeyword(String keyword) {
        // 검색 기록 저장, frequency도 같이 더해서 저장(searchKeyword)
        SearchHistory searchHistory = new SearchHistory();
        searchHistory.setKeyword(keyword);
        searchHistory.setSearchedAt(LocalDateTime.now());
        searchReposirory.save(searchHistory);

        searchKeywordCache.merge(keyword, 1, Integer::sum);
    }

    @Cacheable(value = "searchKeywords")
    public List<String> getTopSearchKeywordsFromNow() {
        // 캐시에서 검색 키워드와 빈도수를 조회하고, 빈도수가 높은 순으로 정렬하여 반환
        return searchKeywordCache.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "searchKeywords", allEntries = true)
    public void clearSearchKeywordsCache() {
        // 캐시에서 모든 검색 키워드 삭제
        searchKeywordCache.clear();
    }

    public List<ItemAutoDto> findItemNamesByKeyword(String keyword, int limit) {
        return itemRepositoryCustom.findItemNamesByKeyword(keyword, limit);
    }

    public CountsAndPriceRangeDto getCountsAndPrice(ItemSearchCondition condition) {
        return itemRepositoryCustom.getCounts(condition);
    }
}
