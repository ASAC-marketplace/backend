package market.demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.demo.domain.member.Member;
import market.demo.domain.member.jwt.JwtFilter;
import market.demo.domain.member.jwt.TokenProvider;
import market.demo.dto.jwt.LoginDto;
import market.demo.dto.jwt.TokenDto;
import market.demo.dto.jwt.MemberDto;
import market.demo.dto.recoverypassword.FindIdDto;
import market.demo.dto.social.CustomOAuth2User;
import market.demo.dto.MemberDeletionRequest;
import market.demo.dto.changememberinfo.CheckMemberInfoDto;
import market.demo.dto.changememberinfo.MemberInfoDto;
import market.demo.dto.changememberinfo.ModifyMemberInfoDto;
import market.demo.dto.social.PasswordVerificationRequestDto;
import market.demo.dto.recoverypassword.PasswordChangeDto;
import market.demo.dto.recoverypassword.RecoveryPasswordRequestDto;
import market.demo.dto.registermember.EmailAvailabilityDto;
import market.demo.dto.registermember.LoginIdAvailabilityDto;
import market.demo.repository.MemberRepository;
import market.demo.service.MemberService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberService memberService;

    //5 일반 회원가입
    @PostMapping("/check-loginid")
    public ResponseEntity<Boolean> checkLoginIdAvailability(@Valid @RequestBody LoginIdAvailabilityDto dto) {
        boolean isAvailable = memberService.checkLoginIdAvailability(dto.getLoginId());
        return ResponseEntity.ok(isAvailable);
    }

    @PostMapping("/check-email")
    public ResponseEntity<Boolean> checkEmailAvailability(@Valid @RequestBody EmailAvailabilityDto dto) {
        boolean isAvailable = memberService.checkEmailAvailability(dto.getEmail());
        return ResponseEntity.ok(isAvailable);
    }

    @PostMapping("/signup")
    public ResponseEntity<MemberDto> signup(
            @Valid @RequestBody MemberDto memberDto
    ) {
        return ResponseEntity.ok(memberService.signup(memberDto));
    }
    //

    //28 회원 탈퇴
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteMember(@Valid @RequestBody MemberDeletionRequest request) {
        memberService.deleteMember(request);
        return ResponseEntity.ok().body("회원 탈퇴 성공");
    }
    //

    //이름 이메일 검증
    @PostMapping("/verify-credentials")
    public ResponseEntity<String> verifyCredentialsInRecoveryPassword(@Valid @RequestBody
                                                                      RecoveryPasswordRequestDto request) {
        memberService.verifyIdAndEmail(request.getLoginId(), request.getEmail());
        return ResponseEntity.ok("OK");
    }

    //아이디 찾기로 수정
    @PostMapping("/findloginid")
    public ResponseEntity<String> findLoginId(@Valid @RequestBody FindIdDto findIdDto) {
        String loginId = memberService.findLoginIdByEmail(findIdDto);
        return ResponseEntity.ok(loginId);
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody PasswordChangeDto passwordChangeDto) {
        memberService.changePassword(passwordChangeDto);
        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }

    //26 api 회원 비밀번호 확인
    @PostMapping("/recheck-password")
    public ResponseEntity<String> recheckPassword(@Valid @RequestBody CheckMemberInfoDto checkMemberInfoDto) {
        memberService.checkPassword(checkMemberInfoDto.getLoginId(), checkMemberInfoDto.getPassword());
        return ResponseEntity.ok("비밀번호 일치합니다.");
    }

    //26 api 개인정보 보내기
    @GetMapping("/modify-member")
    public ResponseEntity<MemberInfoDto> sendMemberInfo(@RequestParam String loginId) {
        MemberInfoDto memberInfoDto = memberService.getMemberInfo(loginId);
        return ResponseEntity.ok(memberInfoDto);
    }

    //26 api 개인정보 수정하기
    @PostMapping("/modify-member")
    public ResponseEntity<String> modifyMemberInfo(@RequestParam String loginId, @RequestBody ModifyMemberInfoDto modifyMemberInfoDto) {
        memberService.modifymember(loginId, modifyMemberInfoDto);
        return ResponseEntity.ok("수정이 완료되었습니다.");
    }


    //40 api소셜 로그인시 회원가입
    @PostMapping("/verify-password")
    public ResponseEntity<?> verifyAndUpdateSocialLogin(
            @Valid
            @RequestBody
            PasswordVerificationRequestDto request) {
        // 비밀번호 검증 및 소셜 로그인 정보 업데이트
        memberService.verifyPassword(request.getPassword(), request.getEmail());
        memberService.updateSocialInfo(request.getEmail(), request.getProvider(), request.getProviderId());
        return ResponseEntity.ok().body("비밀번호 검증 및 소셜 로그인 정보 업데이트 성공");
    }

    @PostMapping("/socialRegister")
    public ResponseEntity<String> registerMember(
            @Valid
            @RequestBody market.demo.dto.social.MemberRegistrationDto
                    request) {
        memberService.socialRegisterNewMember(request, request.getProviderEmail(), request.getProvider(), request.getProviderId());
        return ResponseEntity.ok().body("회원 등록 성공");
    }
    //

    //48 일반 로그인
    @PostMapping("/authenticate")
    public ResponseEntity<TokenDto> authorize(@Valid @RequestBody LoginDto loginDto) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getLoginId(), loginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.createToken(authentication);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);

        return new ResponseEntity<>(new TokenDto(jwt), httpHeaders, HttpStatus.OK);
    }
    //
}

