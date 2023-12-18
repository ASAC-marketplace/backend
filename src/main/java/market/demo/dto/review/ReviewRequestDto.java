package market.demo.dto.review;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequestDto {

    @NotNull
    private Long memberId;
    private Long itemId;
    private String comment;
    private Integer rating;

    private List<String> imageUrls;
}
