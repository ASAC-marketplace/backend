package market.demo.repository;

import jakarta.persistence.Id;
import market.demo.domain.item.Item;
import market.demo.domain.item.Review;
import market.demo.domain.member.Coupon;
import market.demo.domain.member.Member;
import market.demo.domain.search.SearchHistory;
import market.demo.domain.search.SearchKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SearchReposirory extends JpaRepository<SearchHistory, Long> {
//    List<SearchHistory> findById(Id id); Id?

    // 사용횟수를 기준으로 내림차순으로 정렬된 상위 N개의 검색어를 조회하는 쿼리
//    @Query("SELECT s.keyword FROM SearchHistory s GROUP BY s.keyword ORDER BY COUNT(s) DESC")
//    List<String> findTopKeywordsOrderBySearchCountDesc(int topN);

    @Query("SELECT s.keyword AS keyword, COUNT(s) AS frequency FROM SearchHistory s " +
            "GROUP BY s.keyword " +
            "ORDER BY COUNT(s) DESC")
    List<Object[]> findTopKeywords();
}

