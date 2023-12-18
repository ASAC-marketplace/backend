package market.demo.repository;

import market.demo.domain.search.SearchKeyword;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.web.PageableDefault;

import java.util.Optional;

public interface SearchKeywordRepository extends JpaRepository<SearchKeyword, Long> {
    @NotNull Page<SearchKeyword> findAll(@NotNull Pageable pageable);

    Optional<SearchKeyword> findByKeyword(String keyword);
}
