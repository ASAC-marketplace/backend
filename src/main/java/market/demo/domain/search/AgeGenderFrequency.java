package market.demo.domain.search;

import jakarta.persistence.*;
import lombok.Getter;
import market.demo.domain.status.AgeStatus;
import market.demo.domain.status.GenderStatus;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
public class AgeGenderFrequency {
    @Id
    @GeneratedValue
    private Long id;
    private AgeStatus ageRange; // 나이대
    private Integer frequency;
    private GenderStatus gender;//성별

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "search_keyword_id")
    private SearchKeyword searchKeyword;

    public AgeGenderFrequency(SearchKeyword searchKeyword, AgeStatus ageRange, GenderStatus gender){
        this.ageRange = ageRange;
        this.gender = gender;
        this.frequency = 0;
        this.searchKeyword = searchKeyword;

    }

    public void addAgeGenderFrequency(){
        this.frequency += 1;
    }

    public AgeGenderFrequency() {

    }
}
