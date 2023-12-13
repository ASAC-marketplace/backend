package market.demo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import market.demo.domain.item.Review;
import market.demo.domain.search.ItemSearchCondition;
import market.demo.domain.search.SearchHistory;
import market.demo.domain.search.SearchKeyword;
import market.demo.dto.search.ItemSearchResponse;
import market.demo.repository.ItemRepositoryCustom;
import market.demo.repository.SearchReposirory;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class SearchService {
    private final ItemRepositoryCustom itemRepositoryCustom;
    private final SearchReposirory searchReposirory;

   public ItemSearchResponse searchResponse(ItemSearchCondition condition, Pageable pageable) {
       return itemRepositoryCustom.searchPageComplex(condition, pageable);
   }


   public void RecodeSearchKeyword(String keyword) {
       // 검색 기록 저장
       SearchHistory searchHistory = new SearchHistory();
       searchHistory.setKeyword(keyword);
       searchReposirory.save(searchHistory);
   }

   public SearchReposirory frequencyPlus(Long id, int topN) {
       // 1. SearchKeyword에서 검색어 찾기
       Optional<SearchHistory> searchReposirories = searchReposirory.findById(id);

       // 2. 검색 횟수 카운트
       if(searchReposirories.isEmpty()) throw new RuntimeException("검색어가 없습니다.");

       // 3. 상위 N(10)개의 검색어를 결과 객체에 넣어줌
       SearchReposirory searchReposirory1 = searchReposirory;
       searchReposirory1.findTopKeywordsOrderBySearchCountDesc(topN);

       //return frequencyPlus(id, 10);
       return searchReposirory1;

   }


/**
    public ItemSearchResponse searchAaron(Integer age) {

        // 1. 해당 나이의 사진첩이 있는지 확인(DB 호출)
        // 2. 해당 나이의 사진첩 조회
        Photo photo = searchReposirory.findPhotoByAge(age);
        // 1.1 사진첩 유무 확인
        if(photo == null) {
            return new ItemSearchResponse(null, null);
        }


        // 3. 사진첩 데이터에서 결과 객체 생성
        ItemSearchResponse itemSearchResponse = makeResultFromPhoto(photo);
        return itemSearchResponse;


    }
 **/
}
