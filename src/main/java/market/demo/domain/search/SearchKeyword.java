package market.demo.domain.search;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;

import java.util.List;

import static jakarta.persistence.CascadeType.ALL;

@Entity
@Getter
public class SearchKeyword {
    @Id
    @GeneratedValue
    private Long id;

    private String keyword; // 검색 키워드
    private Integer frequency; // 주어진 기간 동안의 검색 빈도수

    @OneToMany(mappedBy = "search_keyword", cascade = ALL)
    private List<AgeGenderFrequency> ageGenderFrequencies;

    public SearchKeyword(String keyword){
        this.keyword = keyword;
        this.frequency = 0;
    }

    public SearchKeyword() {

    }

    public void addFrequency(){
        this.frequency += 1;
    }
}
