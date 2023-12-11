package market.demo.dto.jwt;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import market.demo.domain.member.Member;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {

    @NotNull
    @NotEmpty
    @Size(min = 3, max = 50)
    private String memberName;

    @NotNull
    @Size(min = 3, max = 50)
    private String loginId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    @Size(min = 3, max = 100)
    private String password;

    @NotNull
    @Email
    private String email;

    private Set<AuthorityDto> authorityDtoSet;

    public static MemberDto from(Member member) {
        if (member == null) return null; 

        return MemberDto.builder()
                .memberName(member.getMemberName()) // 멤버 이름 추가
                .loginId(member.getLoginId())
                .email(member.getEmail())
                .authorityDtoSet(member.getAuthorities().stream()
                        .map(authority -> AuthorityDto.builder().authorityName(authority.getAuthorityName()).build())
                        .collect(Collectors.toSet()))
                .build();
    }
}
