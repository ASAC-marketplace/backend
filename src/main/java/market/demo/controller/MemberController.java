package market.demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.demo.domain.member.Member;
import market.demo.dto.social.CustomOAuth2User;
import market.demo.dto.MemberDeletionRequest;
import market.demo.dto.changememberinfo.CheckMemberInfoDto;
import market.demo.dto.changememberinfo.MemberInfoDto;
import market.demo.dto.changememberinfo.ModifyMemberInfoDto;
import market.demo.dto.social.PasswordVerificationRequestDto;
import market.demo.dto.recoverypassword.PasswordChangeDto;
import market.demo.dto.recoverypassword.RecoveryPasswordRequestDto;
import market.demo.dto.registermember.EmailAvailabilityDto;
import market.demo.dto.registermember.MemberRegistrationDto;
import market.demo.dto.registermember.LoginIdAvailabilityDto;
import market.demo.repository.MemberRepository;
import market.demo.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

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
    @PostMapping("/register")
    public ResponseEntity<Member> registerMember(@RequestBody MemberRegistrationDto registrationDto) {
        Member member = memberService.registerMember(registrationDto);
        return ResponseEntity.ok(member);
    }
    //

    //28 회원 탈퇴
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteMember(@RequestBody MemberDeletionRequest request) {
        memberService.deleteMember(request);
        return ResponseEntity.ok().body("회원 탈퇴 성공");
    }
    //

    @PostMapping("/verify-credentials")
    public ResponseEntity<String> verifyCredentialsInRecoveryPassword(@RequestBody RecoveryPasswordRequestDto request) {
        boolean exists = memberService.isMemberExists(request.getLoginId(), request.getEmail());
        if (exists) {
            return ResponseEntity.ok("OK");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 실패");
        }
    }

    //36 비밀번호 찾기..
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody PasswordChangeDto passwordChangeDto) {
        boolean isPasswordChanged = memberService.changePassword(passwordChangeDto);
        if (!isPasswordChanged) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }
        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }

    //26 api 회원 비밀번호 확인
    @PostMapping("/recheck-password")
    public ResponseEntity<String> recheckPassword(@RequestBody CheckMemberInfoDto checkMemberInfoDto){
        memberService.checkPassword(checkMemberInfoDto.getLoginId(), checkMemberInfoDto.getPassword());
        return ResponseEntity.ok("비밀번호 일치합니다.");
    }

    //26 api 개인정보 보내기
    @GetMapping("/modify-member")
    public ResponseEntity<MemberInfoDto> sendMemberinfo(@RequestParam String loginId){
        MemberInfoDto memberInfoDto = memberService.getMemberinfo(loginId);
        return ResponseEntity.ok(memberInfoDto);
    }

    //26 api 수정하기
    @PostMapping("/modify-member")
    public ResponseEntity<String> modifyMemberinfo(@RequestParam String loginId, @RequestBody ModifyMemberInfoDto modifyMemberInfoDto){
        memberService.modifymember(loginId, modifyMemberInfoDto);
        return ResponseEntity.ok("수정이 완료되었습니다.");
    }


    //40 api소셜 로그인시 회원가입
    @PostMapping("/verify-password")
    public ResponseEntity<?> verifyAndUpdateSocialLogin(@AuthenticationPrincipal CustomOAuth2User customUser,
                                            @RequestBody PasswordVerificationRequestDto request) {
        if (customUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자 인증 실패");
        }
        // 비밀번호 검증 및 소셜 로그인 정보 업데이트
        memberService.verifyPassword(customUser.getName(), request.getPassword());
        memberService.updateSocialInfo(customUser.getEmail(), customUser.getProvider(), customUser.getProviderId());
        return ResponseEntity.ok().body("비밀번호 검증 및 소셜 로그인 정보 업데이트 성공");
    }


    @PostMapping("/socialRegister")
    public ResponseEntity<String> registerMember(@AuthenticationPrincipal CustomOAuth2User customUser,
                                                 @RequestBody market.demo.dto.social.MemberRegistrationDto request) {
        memberService.socialRegisterNewMember(request, customUser.getEmail(), customUser.getProvider(), customUser.getProviderId());
        return ResponseEntity.ok().body("회원 등록 성공");
    }
    //
}

