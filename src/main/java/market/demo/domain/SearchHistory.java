package market.demo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class SearchHistory {
    @Id
    @GeneratedValue
    @Column(name = "keword_id")
    private Long id;

    @Column(name = "keyword")
    private String keyword; // 검색 키워드

    @Column(name = "searched_at")
    private LocalDateTime searchedAt; // 검색 시간
}
