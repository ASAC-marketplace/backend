package market.demo.service;

import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.demo.domain.member.Member;
import market.demo.domain.search.AgeGenderFrequency;
import market.demo.domain.search.ItemSearchCondition;
import market.demo.domain.search.SearchHistory;
import market.demo.domain.search.SearchKeyword;
import market.demo.dto.search.CountsAndPriceRangeDto;
import market.demo.dto.search.ItemAutoDto;
import market.demo.dto.search.ItemRecommendDto;
import market.demo.dto.search.ItemSearchResponse;
import market.demo.exception.MemberNotFoundException;
import market.demo.repository.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.redis.core.StringRedisTemplate;
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
@Slf4j
@Transactional
@RequiredArgsConstructor
public class SearchService {
    private final ItemRepositoryCustom itemRepositoryCustom;
    private final SearchReposirory searchReposirory;
    private final StringRedisTemplate redisTemplate;
    private final MemberRepository memberRepository;
    private final SearchKeywordRepository searchKeywordRepository;
    private final AgeGenderFrequencyRepository ageGenderFrequencyRepository;

    private static final Map<String, Integer> searchKeywordCache = new ConcurrentHashMap<>();

    public ItemSearchResponse searchResponse(ItemSearchCondition condition, Pageable pageable) {
        return itemRepositoryCustom.searchPageComplex(condition, pageable);
    }

//    @CachePut(value = "searchKeywords", key = "#keyword")
    public void recodeSearchKeyword(String keyword) {
        SearchHistory searchHistory = new SearchHistory();
        searchHistory.setKeyword(keyword);
        searchHistory.setSearchedAt(LocalDateTime.now());
        searchReposirory.save(searchHistory);

        searchKeywordCache.merge(keyword, 1, Integer::sum);
    }

//    @Cacheable(value = "searchKeywords")
    public List<String> getTopSearchKeywordsFromNow() {
        // 캐시에서 검색 키워드와 빈도수를 조회하고, 빈도수가 높은 순으로 정렬하여 반환
        return searchKeywordCache.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

   public CountsAndPriceRangeDto getCountsAndPrice(ItemSearchCondition condition) {
       return itemRepositoryCustom.getCounts(condition);
   }

    private @Nullable Member getMemberByLoginId(String loginId) {
       if(loginId == null) return null;
       return memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new MemberNotFoundException("사용자를 찾을 수 없습니다."));
    }

    private Page<SearchKeyword> getKeywordByMember(Member member){
        Sort sort = Sort.by("frequency").descending();
        Pageable pageable = PageRequest.of(0, 8, sort);
        if (member == null)
            return searchKeywordRepository.findAll(pageable);
        else{
            log.info("MEMBER: "+ member.getAgeRange());
            return ageGenderFrequencyRepository.findAllByAgeRangeAndGender(member.getAgeRange(), member.getGender(), pageable);
        }
    }

    public List<ItemRecommendDto> getRecommendKeyword(String loginId) {
        Member member = getMemberByLoginId(loginId);
        Page<SearchKeyword> searchKeywords = getKeywordByMember(member);

        return mapToItemRecommendDtoList(searchKeywords);
    }

    private List<ItemRecommendDto> mapToItemRecommendDtoList(@NotNull Page<SearchKeyword> searchKeywords) {
        return searchKeywords.stream()
                .map(ItemRecommendDto::new)
                .collect(Collectors.toList());
    }

    // 검색 시 searchkeyword 추가
    public void insertSearchKeywordByMember(String keyword, String loginId){
       Member member = getMemberByLoginId(loginId);
       SearchKeyword searchKeyword = getSearchKeyword(keyword);

       searchKeyword.addFrequency();
       searchKeywordRepository.save(searchKeyword);

       if(member != null){
           AgeGenderFrequency ageGenderFrequency = getAgeGenderFrequency(member, searchKeyword);
           ageGenderFrequency.addAgeGenderFrequency();
           ageGenderFrequencyRepository.save(ageGenderFrequency);
       }
    }

    private SearchKeyword getSearchKeyword(String keyword){
       return searchKeywordRepository.findByKeyword(keyword).orElseGet(() -> new SearchKeyword(keyword));
    }
    private AgeGenderFrequency getAgeGenderFrequency(@NotNull Member member, SearchKeyword searchKeyword){
        return ageGenderFrequencyRepository
                .findBySearchKeywordAndAgeRangeAndGender(searchKeyword, member.getAgeRange(), member.getGender())
                .orElseGet(() -> new AgeGenderFrequency(searchKeyword, member.getAgeRange(), member.getGender()));
    }

//    @CacheEvict(value = "searchKeywords", allEntries = true)
    public void clearSearchKeywordsCache() {
        // 캐시에서 모든 검색 키워드 삭제
        searchKeywordCache.clear();
    }

    public List<ItemAutoDto> findItemNamesByKeyword(String keyword, int limit) {
        return itemRepositoryCustom.findItemNamesByKeyword(keyword, limit);
    }
}
