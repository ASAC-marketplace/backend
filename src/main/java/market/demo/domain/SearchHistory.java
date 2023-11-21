package market.demo.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class SearchHistory {
    @Id
    @GeneratedValue
    private Long id;

    private String keyword; // 검색 키워드
    private LocalDateTime searchedAt; // 검색 시간

//    @ManyToOne
//    private User user; // 검색한 사용자 (선택 사항)
}
