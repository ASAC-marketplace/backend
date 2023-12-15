package market.demo.repository;

import io.lettuce.core.dynamic.annotation.Param;
import market.demo.domain.search.AgeGenderFrequency;
import market.demo.domain.search.SearchKeyword;
import market.demo.domain.status.AgeStatus;
import market.demo.domain.status.GenderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

public interface AgeGenderFrequencyRepository extends JpaRepository<AgeGenderFrequency, Long> {
    Optional<AgeGenderFrequency> findBySearchKeywordAndAgeRangeAndGender(SearchKeyword searchKeyword, AgeStatus ageStatus, GenderStatus genderStatus);

    @Query("SELECT agf.searchKeyword, agf.frequency as frequency " +
            "FROM AgeGenderFrequency agf " +
            "WHERE agf.ageRange = :age AND agf.gender = :gender " +
            "ORDER BY agf.frequency DESC")
    List<Object[]> findTop8KeywordsByFrequency(
            @Param("age") AgeStatus age,
            @Param("gender") GenderStatus gender,
            Pageable pageable
    );
}
