package market.demo.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.demo.domain.member.Member;
import market.demo.domain.member.jwt.Authority;
import market.demo.domain.status.OrderStatus;
import market.demo.dto.MemberDeletionRequest;
import market.demo.dto.changememberinfo.MemberInfoDto;
import market.demo.dto.changememberinfo.ModifyMemberInfoDto;
import market.demo.dto.jwt.MemberDto;
import market.demo.dto.recoverypassword.FindIdDto;
import market.demo.dto.recoverypassword.PasswordChangeDto;
import market.demo.dto.registermember.MemberRegistrationDto;
import market.demo.exception.DuplicateMemberException;
import market.demo.exception.InvalidPasswordException;
import market.demo.exception.MemberNotFoundException;
import market.demo.exception.NotFoundMemberException;
import market.demo.repository.MemberRepository;
import market.demo.service.jwt.SecurityUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

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

        // 주문 상태 확인
        boolean hasActiveOrders = member.getOrders().stream()
                .anyMatch(order -> order.getOrderStatus() == OrderStatus.PENDING || order.getOrderStatus() == OrderStatus.PROCESSING);

        if (hasActiveOrders) {
            throw new IllegalStateException("활성 주문이 존재하여 회원 탈퇴가 불가능합니다.");
        }

        memberRepository.delete(member);
    }

    public boolean isMemberExists(String loginId, String email) {
        return memberRepository.existsByLoginIdAndEmail(loginId, email);
    }

//    public boolean changeId(IdChangeDto idChangeDto) {
//        if (!idChangeDto.getNewId().equals(idChangeDto.getConfirmId())) {
//            throw new IllegalArgumentException("아이디가 일치하지 않습니다.");
//        }
//
//        Member member = memberRepository.findByLoginId((idChangeDto.getLoginId())).get();
//        if (member == null) {
//            return false;
//        }
//        return true;
//    }

    public void changePassword(PasswordChangeDto passwordChangeDto) {
        if (!passwordChangeDto.getNewPassword().equals(passwordChangeDto.getConfirmPassword())) {
            throw new IllegalArgumentException("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
        }

        Member member = memberRepository.findByLoginId(passwordChangeDto.getLoginId())
                .orElseThrow(()-> new MemberNotFoundException("사용자를 찾을 수 없습니다"));

        String encodedPassword = passwordEncoder.encode(passwordChangeDto.getNewPassword());
        member.updatePassword(encodedPassword, passwordEncoder);
        memberRepository.save(member);
    }

    public String findLoginIdByEmail(FindIdDto findIdDto) {
        Member member = memberRepository.findByEmailAndMemberName(findIdDto.getEmail(),
                        findIdDto.getMemberName())
                .orElseThrow(() -> new MemberNotFoundException("사용자를 찾을 수 없습니다."));
        return member.getLoginId();
    }

    public void checkPassword(String loginId, String password){
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new MemberNotFoundException("사용자를 찾을 수 없습니다"));

        if(!member.getPassword().equals(password)) throw new InvalidPasswordException("비밀번호가 맞지 않습니다.");
    }

    public MemberInfoDto getMemberinfo(String loginId) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new MemberNotFoundException("사용자를 찾을 수 없습니다"));

        MemberInfoDto memberInfoDto = new MemberInfoDto();
        BeanUtils.copyProperties(member, memberInfoDto);

        return memberInfoDto;
    }

    public void modifymember(String loginId, ModifyMemberInfoDto modifyMemberInfoDto) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new MemberNotFoundException("사용자를 찾을 수 없습니다"));

        // 비밀번호 확인 로직
        if (!member.getPassword().equals(modifyMemberInfoDto.getPassword())) throw new InvalidPasswordException("비밀번호가 맞지 않습니다.");

        //새 비밀번호 확인
        if(!modifyMemberInfoDto.getNewPassword().equals(modifyMemberInfoDto.getNewPasswordCheck())){
            throw  new IllegalArgumentException("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
        }

        //회원 정보 수정
        if(modifyMemberInfoDto.getNewPassword().isEmpty()) member.setPassword(modifyMemberInfoDto.getPassword());
        else member.setPassword(modifyMemberInfoDto.getNewPassword());

        member.setLoginId(modifyMemberInfoDto.getLoginId());
        member.setEmail(modifyMemberInfoDto.getEmail());
        member.setPhoneNumber(modifyMemberInfoDto.getPhoneNumber());
        member.setMemberName(modifyMemberInfoDto.getMemberName());

       memberRepository.save(member);
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
                registrationDto.getMemberName(),
                email,
                registrationDto.getLoginId(),
                passwordEncoder.encode(registrationDto.getPassword()),
                registrationDto.getPhoneNumber(),
                provider,
                providerId
        );
        memberRepository.save(member);
    }

    @Transactional
    public MemberDto signup(MemberDto memberDto) {
        if (memberRepository.findOneWithAuthoritiesByLoginId(memberDto.getLoginId()).orElse(null) != null) {
            throw new DuplicateMemberException("이미 가입되어 있는 유저입니다.");
        }

        if (memberRepository.findByEmail(memberDto.getEmail()).isPresent()) {
            throw new DuplicateMemberException("이미 사용중인 이메일입니다.");
        }

        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        Member user = Member.builder()
                .memberName(memberDto.getMemberName())
                .loginId(memberDto.getLoginId())
                .password(passwordEncoder.encode(memberDto.getPassword()))
                .email(memberDto.getEmail())
                .authorities(Collections.singleton(authority))
                .build();

        return MemberDto.from(memberRepository.save(user));
    }

    @Transactional(readOnly = true)
    public MemberDto getUserWithAuthorities(String username) {
        return MemberDto.from(memberRepository.findOneWithAuthoritiesByLoginId(username).orElse(null));
    }

    @Transactional(readOnly = true)
    public MemberDto getMyUserWithAuthorities() {
        return MemberDto.from(
                SecurityUtil.getCurrentUsername()
                        .flatMap(memberRepository::findOneWithAuthoritiesByLoginId)
                        .orElseThrow(() -> new NotFoundMemberException("Member not found"))
        );
    }
}
