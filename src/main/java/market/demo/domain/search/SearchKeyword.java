package market.demo.domain.search;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class SearchKeyword {
    @Id
    @GeneratedValue
    private Long id;

    private String keyword; // 검색 키워드
    private Integer frequency; // 주어진 기간 동안의 검색 빈도수
}
