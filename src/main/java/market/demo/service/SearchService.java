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
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
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

   public ItemSearchResponse searchResponse(ItemSearchCondition condition, Pageable pageable) {
       return itemRepositoryCustom.searchPageComplex(condition, pageable);
   }

   public void recodeSearchKeyword(String keyword) {
       // 검색 기록 저장, frequency도 같이 더해서 저장(searchKeyword)
       SearchHistory searchHistory = new SearchHistory();
       searchHistory.setKeyword(keyword);
       searchHistory.setSearchedAt(LocalDateTime.now());
       searchReposirory.save(searchHistory);

       // Redis에 검색 키워드 캐시 (증가 연산 사용)
       redisTemplate.opsForValue().increment("search:keyword:" + keyword);
       redisTemplate.expire("search:keyword:" + keyword, Duration.ofHours(1)); // TTL 1시간 설정
   }

    public List<String> getTopSearchKeywordsFromNow() {
        // Redis에서 캐시된 키워드 조회
        Set<String> keys = redisTemplate.keys("search:keyword:*");
        Map<String, Integer> keywordCounts = new HashMap<>();

        for (String key : keys) {
            String keyword = key.substring("search:keyword:".length());
            Integer count = Integer.parseInt(redisTemplate.opsForValue().get(key));
            keywordCounts.put(keyword, count);
        }

        // 가장 많이 검색된 키워드 순으로 정렬
        return keywordCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
   public List<ItemAutoDto> findItemNamesByKeyword(String keyword, int limit) {
       return itemRepositoryCustom.findItemNamesByKeyword(keyword, limit);
   }

   public CountsAndPriceRangeDto getCountsAndPrice(ItemSearchCondition condition) {
       return itemRepositoryCustom.getCounts(condition);
   }

    private @Nullable Member getMemberByLoginId(@NotNull String loginId) {
       if(loginId.isBlank()) return null;
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

}
