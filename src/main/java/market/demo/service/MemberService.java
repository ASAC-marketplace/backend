package market.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.demo.domain.member.Member;
import market.demo.dto.MemberDeletionRequest;
import market.demo.dto.changememberinfo.CheckMemberInfoDto;
import market.demo.dto.changememberinfo.MemberInfoDto;
import market.demo.dto.changememberinfo.ModifyMemberInfoDto;
import market.demo.dto.recoverypassword.PasswordChangeDto;
import market.demo.dto.registermember.MemberRegistrationDto;
import market.demo.exception.InvalidPasswordException;
import market.demo.exception.MemberNotFoundException;
import market.demo.repository.MemberRepository;
import org.hibernate.annotations.Check;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean checkLoginIdAvailability(String loginId) {
        return !memberRepository.existsByLoginId(loginId);
    }

    public boolean checkEmailAvailability(String email) {
        return !memberRepository.existsByEmail(email);
    }

    public Member registerMember(MemberRegistrationDto registrationDto) {
        if (memberRepository.existsByLoginId(registrationDto.getLoginId())
                || memberRepository.existsByEmail(registrationDto.getEmail())) {
            throw new IllegalStateException("이미 사용중인 로그인 ID 또는 이메일입니다.");
        }

        Member member = Member.builder()
                .loginId(registrationDto.getLoginId())
                .memberName(registrationDto.getMemberName())
                .email(registrationDto.getEmail())
                .password(passwordEncoder.encode(registrationDto.getPassword()))
                .phoneNumber(registrationDto.getPhoneNumber())
                .build();

        return memberRepository.save(member);
    }

    public void deleteMember(MemberDeletionRequest deletionRequest) {
        Member member = memberRepository.findById(deletionRequest.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException("멤버를 찾을 수 없습니다. ID: " + deletionRequest.getMemberId()));
        memberRepository.delete(member);
    }

    public boolean isMemberExists(String loginId, String email) {
        return memberRepository.existsByLoginIdAndEmail(loginId, email);
    }

//    // 인증번호 발송 로직 (실제 구현 필요)
//    public void sendVerificationCode(String phoneNumber) {
//        // SMS 서비스를 통한 인증번호 발송 로직
//    }

    public boolean changePassword(PasswordChangeDto passwordChangeDto) {
        if (!passwordChangeDto.getNewPassword().equals(passwordChangeDto.getConfirmPassword())) {
            throw new IllegalArgumentException("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
        }

        Member member = memberRepository.findByLoginId(passwordChangeDto.getLoginId());
        if (member == null) {
            return false;
        }

        String encodedPassword = passwordEncoder.encode(passwordChangeDto.getNewPassword());
        member.updatePassword(encodedPassword, passwordEncoder);
        memberRepository.save(member);
        return true;
    }

    public boolean checkPassword(String loginId, String password){
        Member member = memberRepository.findByLoginId(loginId);

        return member.getPassword().equals(password);
    }

    public MemberInfoDto sendMemberinfo(String loginId) {
        Member member = memberRepository.findByLoginId(loginId);

        MemberInfoDto memberInfoDto = new MemberInfoDto();
        memberInfoDto.setLoginId(member.getLoginId());
        memberInfoDto.setMemberName(member.getMemberName());
        memberInfoDto.setEmail(member.getEmail());
        memberInfoDto.setPhoneNumber(member.getPhoneNumber());

        return memberInfoDto;
    }

    public boolean modifymember(String loginId, ModifyMemberInfoDto modifyMemberInfoDto) {
        Member member = memberRepository.findByLoginId(loginId);

        if(member == null) return false;

        if(!modifyMemberInfoDto.getNewPassword().isEmpty()) member.setPassword(modifyMemberInfoDto.getNewPassword());
        else member.setPassword(modifyMemberInfoDto.getPassword());

        member.setLoginId(modifyMemberInfoDto.getLoginId());
        member.setEmail(modifyMemberInfoDto.getEmail());
        member.setPhoneNumber(modifyMemberInfoDto.getPhoneNumber());
        member.setMemberName(modifyMemberInfoDto.getMemberName());

       memberRepository.save(member);
       return true;
    }

    public void verifyPassword(String email, String password) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new InvalidPasswordException("비밀번호가 일치하지 않습니다.");
        }
    }

    public void updateSocialInfo(String email, String provider, String providerId) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        member.updateSocialLoginInfo(provider, providerId);
        memberRepository.save(member);
    }

    public void socialRegisterNewMember(market.demo.dto.social.MemberRegistrationDto registrationDto, String email, String provider, String providerId) {
        Member member = Member.createMemberWithProviderInfo(
                email,
                registrationDto.getLoginId(),
                passwordEncoder.encode(registrationDto.getPassword()),
                registrationDto.getPhoneNumber(),
                provider,
                providerId
        );
        memberRepository.save(member);

    }
}
