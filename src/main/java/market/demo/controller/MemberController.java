package market.demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import market.demo.domain.Member;
import market.demo.dto.MemberDeletionRequest;
import market.demo.dto.changememberinfo.CheckMemberInfoDto;
import market.demo.dto.changememberinfo.MemberInfoDto;
import market.demo.dto.changememberinfo.ModifyMemberInfoDto;
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

    //26 api 회원 비밀번호 확인
    @PostMapping("/recheck-password")
    public ResponseEntity<String> recheckPassword(@RequestBody CheckMemberInfoDto checkMemberInfoDto){
        boolean isPasswordCorrect = memberService.checkPassword(checkMemberInfoDto.getLoginId(), checkMemberInfoDto.getPassword());

        if(!isPasswordCorrect){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("비밀번호가 맞지 않습니다.");
        }
        return ResponseEntity.ok("비밀번호 일치합니다.");
    }

    //26 api 개인정보 보내기
    @GetMapping("/modify-member")
    public ResponseEntity<MemberInfoDto> sendMemberinfo(@RequestParam String loginId){
        MemberInfoDto memberInfoDto = MemberService.getMemberinfo(loginId);

        return ResponseEntity.ok(memberInfoDto);
    }

    //26 api 수정하기
    @PostMapping("/modify-member")
    public ResponseEntity<String> modifyMemberinfo(@RequestParam String loginId, @RequestBody ModifyMemberInfoDto modifyMemberInfoDto){
        //현재 비밀번호 확인
        if(!memberService.checkPassword(loginId, modifyMemberInfoDto.getPassword())){
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("비밀번호가 맞지 않습니다");
        }

        //새 비밀번호 확인
        if(!modifyMemberInfoDto.getNewPassword().equals(modifyMemberInfoDto.getNewPassword_check())){
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("새로운 비밀번호가 서로 일치하지 않습니다");
        }

        //회원 정보 수정
        if(!memberService.modifymember(loginId, modifyMemberInfoDto)){
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("회원 정보를 찾을 수 없습니다.");
        }

        return ResponseEntity.ok("수정이 완료되었습니다.");
    }


}

