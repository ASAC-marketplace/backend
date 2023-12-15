package market.demo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import market.demo.domain.member.Member;
import market.demo.domain.search.AgeGenderFrequency;
import market.demo.domain.search.ItemSearchCondition;
import market.demo.domain.search.SearchKeyword;
import market.demo.dto.search.*;
import market.demo.domain.search.SearchHistory;
import market.demo.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
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

    private Member getMemberByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId).orElseGet(Member::new);
    }

    private List<Object[]> getKeywordByMember(Member member){
        if (member == null)
            return searchKeywordRepository.findTop8KeywordsByFrequency();
        else{
            return ageGenderFrequencyRepository.findTop8KeywordsByFrequency(member.getAgeRange(), member.getGender(),
                    (java.awt.print.Pageable) PageRequest.of(0, 8));
        }
        //고쳐야함
    }

    public List<ItemRecommendDto> getRecommendKeyword(String loginId) {
        Member member = getMemberByLoginId(loginId);
        List<Object[]> keywordsByFrequency = getKeywordByMember(member);

        return mapToItemRecommendDtoList(keywordsByFrequency);
    }

    private List<ItemRecommendDto> mapToItemRecommendDtoList(List<Object[]> keywords) {
        return keywords.stream()
                .map(ItemRecommendDto::new)
                .collect(Collectors.toList());
    }

    // 검색 시 searchkeyword 추가
    public void insertSearchKeywordByMember(String keyword, Member member){
       SearchKeyword searchKeyword = searchKeywordRepository.findByKeyword(keyword)
               .orElseGet(() -> new SearchKeyword(keyword));

        searchKeyword.addFrequency();
       if(member != null){
           searchKeyword.addFrequency();
           AgeGenderFrequency ageGenderFrequency = ageGenderFrequencyRepository
                   .findBySearchKeywordAndAgeRangeAndGender(searchKeyword, member.getAgeRange(), member.getGender())
                   .orElseGet(() -> new AgeGenderFrequency(searchKeyword, member.getAgeRange(), member.getGender()));

           ageGenderFrequency.addAgeGenderFrequency();
       }
    }

}
