package market.demo.service;

import lombok.RequiredArgsConstructor;
import market.demo.domain.Member;
import market.demo.dto.registermember.MemberRegistrationDto;
import market.demo.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
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

        Member member = new Member(
                registrationDto.getLoginId(),
                registrationDto.getMemberName(),
                registrationDto.getEmail(),
                passwordEncoder.encode(registrationDto.getPassword()),
                registrationDto.getPhoneNumber()
        );
        return memberRepository.save(member);
    }

//    // 인증번호 발송 로직 (실제 구현 필요)
//    public void sendVerificationCode(String phoneNumber) {
//        // SMS 서비스를 통한 인증번호 발송 로직
//    }
}
