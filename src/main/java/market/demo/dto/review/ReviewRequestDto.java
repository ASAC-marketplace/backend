package market.demo.dto.review;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import market.demo.advice.MemberIdAwardDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequestDto implements MemberIdAwardDto {

    @NotNull
    private Long memberId;
    private Long itemId;
    private String comment;
    private Integer rating;

    private List<String> imageUrls;

    @Override
    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }
}
