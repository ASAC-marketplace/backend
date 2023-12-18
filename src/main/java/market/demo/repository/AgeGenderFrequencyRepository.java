package market.demo.repository;

import io.lettuce.core.dynamic.annotation.Param;
import market.demo.domain.search.AgeGenderFrequency;
import market.demo.domain.search.SearchKeyword;
import market.demo.domain.status.AgeStatus;
import market.demo.domain.status.GenderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.web.PageableDefault;

import java.util.List;
import java.util.Optional;

public interface AgeGenderFrequencyRepository extends JpaRepository<AgeGenderFrequency, Long> {
    Optional<AgeGenderFrequency> findBySearchKeywordAndAgeRangeAndGender(SearchKeyword searchKeyword, AgeStatus ageStatus, GenderStatus genderStatus);

    @Query("select arg.searchKeyword from AgeGenderFrequency as arg")
    Page<SearchKeyword> findAllByAgeRangeAndGender( AgeStatus ageStatus, GenderStatus genderStatus, Pageable pageable);
}
