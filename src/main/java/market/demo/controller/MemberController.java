package market.demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import market.demo.domain.Member;
import market.demo.dto.MemberDeletionRequest;
import market.demo.dto.recoverypassword.PasswordChangeDto;
import market.demo.dto.recoverypassword.RecoveryPasswordRequestDto;
import market.demo.dto.registermember.EmailAvailabilityDto;
import market.demo.dto.registermember.MemberRegistrationDto;
import market.demo.dto.registermember.LoginIdAvailabilityDto;
import market.demo.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
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
}
