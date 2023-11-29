package market.demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.demo.domain.Member;
import market.demo.dto.CustomOAuth2User;
import market.demo.dto.MemberDeletionRequest;
import market.demo.dto.PasswordVerificationRequestDto;
import market.demo.dto.recoverypassword.PasswordChangeDto;
import market.demo.dto.recoverypassword.RecoveryPasswordRequestDto;
import market.demo.dto.registermember.EmailAvailabilityDto;
import market.demo.dto.registermember.MemberRegistrationDto;
import market.demo.dto.registermember.LoginIdAvailabilityDto;
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

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteMember(@RequestBody MemberDeletionRequest request) {
        memberService.deleteMember(request);
        return ResponseEntity.ok().body("회원 탈퇴 성공");
    }

    @PostMapping("/verify-credentials")
    public ResponseEntity<String> verifyCredentialsInRecoveryPassword(@RequestBody RecoveryPasswordRequestDto request) {
        boolean exists = memberService.isMemberExists(request.getLoginId(), request.getEmail());
        if (exists) {
            return ResponseEntity.ok("OK");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 실패");
        }
    }
//    @PostMapping("/send-verification-code")
//    public ResponseEntity<Void> sendVerificationCode(@RequestBody PhoneNumberVerificationDto dto) {
//        memberService.sendVerificationCode(dto.getPhoneNumber());
//        return ResponseEntity.ok().build();
//    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody PasswordChangeDto passwordChangeDto) {
        boolean isPasswordChanged = memberService.changePassword(passwordChangeDto);

        if (!isPasswordChanged) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }

        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }

    //    @PostMapping("/verify-password")
//    public ResponseEntity<?> verifyPassword(@AuthenticationPrincipal CustomOAuth2User customUser,
//                                            @RequestBody PasswordVerificationRequestDto request) {
//        // CustomOAuth2User에서 기존 회원 정보 추출
//
//        // customUser 객체 상태 로깅
//        if (customUser == null) {
//            log.error("verifyPassword 호출 시 customUser가 null입니다.");
//            // 여기서 적절한 예외 처리 또는 응답 반환
//        } else {
//            log.info("verifyPassword 호출 시 customUser 상태: {}", customUser.toString());
//            // customUser가 null이 아닌 경우의 나머지 로직
//        }
//
//
//        assert customUser != null;
//        Member member = customUser.getExistingMember();
//
//        log.info("at controller Member Info: {}", member);
//
//
//        // 비밀번호 검증
//        memberService.verifyPassword(member.getEmail(), request.getPassword());
//
//        // 소셜 로그인 정보 업데이트
//        memberService.updateSocialInfo(member, customUser.getAttributes().get("providerId").toString(),
//                customUser.getAttributes().get("provider").toString());
//
//        return ResponseEntity.ok().build();
//    }
    @PostMapping("/verify-password")
    public ResponseEntity<?> verifyPassword(@AuthenticationPrincipal CustomOAuth2User customUser,
                                            @RequestBody PasswordVerificationRequestDto request) {
        if (customUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자 인증 실패");
        }

        try {
            memberService.verifyPassword(customUser.getName(), request.getPassword());
            return ResponseEntity.ok().body("비밀번호 검증 성공");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("비밀번호 검증 실패: " + e.getMessage());
        }
    }
}
