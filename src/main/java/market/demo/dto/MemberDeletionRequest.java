package market.demo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MemberDeletionRequest {
    @NotNull
    private Long memberId;
}
