package market.demo.repository;

import io.lettuce.core.dynamic.annotation.Param;
import market.demo.domain.member.Member;
import market.demo.domain.search.SearchKeyword;
import market.demo.domain.status.AgeStatus;
import market.demo.domain.status.GenderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SearchKeywordRepository extends JpaRepository<SearchKeyword, Long> {
    @Query("SELECT sk.keyword, sk.frequency " +
            "FROM SearchKeyword sk " +
            "ORDER BY sk.frequency DESC")
    List<Object[]> findTop8KeywordsByFrequency();

    Optional<SearchKeyword> findByKeyword(String keyword);
}
