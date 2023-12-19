package market.demo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import market.demo.advice.MemberIdAwardDto;

@Data
public class MemberDeletionRequest implements MemberIdAwardDto {
    @NotNull
    private Long memberId;
}
