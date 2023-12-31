package market.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.demo.domain.etc.Wishlist;
import market.demo.domain.member.Member;
import market.demo.domain.member.jwt.Authority;
import market.demo.domain.status.OrderStatus;
import market.demo.dto.MemberDeletionRequest;
import market.demo.dto.changememberinfo.MemberInfoDto;
import market.demo.dto.changememberinfo.ModifyMemberInfoDto;
import market.demo.dto.itemdetailinfo.CouponDto;
import market.demo.dto.itemdetailinfo.WishDto;
import market.demo.dto.jwt.MemberDto;
import market.demo.dto.mypage.MyPageDto;
import market.demo.dto.recoverypassword.FindIdDto;
import market.demo.dto.recoverypassword.PasswordChangeDto;
import market.demo.dto.registermember.MemberRegistrationDto;
import market.demo.dto.social.CustomOAuth2User;
import market.demo.exception.*;
import market.demo.repository.MemberRepository;
import market.demo.service.jwt.SecurityUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

        //찜하기 추가
        member.setWishlist(new Wishlist(member));
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

        member.updatePassword(passwordChangeDto.getNewPassword(), passwordEncoder);
        memberRepository.save(member);
    }

    public String findLoginIdByEmail(FindIdDto findIdDto) {
        Member member = memberRepository.findByEmailAndMemberName(findIdDto.getEmail(),
                        findIdDto.getMemberName())
                .orElseThrow(() -> new MemberNotFoundException("사용자를 찾을 수 없습니다."));
        return member.getLoginId();
    }

    public void checkPassword(String loginId, String password){
        Member member = getMemberByLoginId(loginId);
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new InvalidPasswordException("비밀번호가 일치하지 않습니다.");
        }
    }

    private Member getMemberByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new MemberNotFoundException("사용자를 찾을 수 없습니다. 로그인 해주세요"));
    }

    public MemberInfoDto getMemberInfo(String loginId) {
        Member member = getMemberByLoginId(loginId);
        return new MemberInfoDto(member);
    }

    public void modifyMember(String loginId, ModifyMemberInfoDto modifyMemberInfoDto) {
        Member member = getMemberByLoginId(loginId);

        // 비밀번호 확인 로직
        if (!passwordEncoder.matches(modifyMemberInfoDto.getPassword(), member.getPassword())) {
            throw new InvalidPasswordException("비밀번호가 일치하지 않습니다.");
        }

        //새 비밀번호 변경
        if(!modifyMemberInfoDto.getNewPassword().isEmpty()) {
            modifyMemberInfoDto.checkNewPassword();
            member.updatePassword(modifyMemberInfoDto.getNewPassword(), passwordEncoder);
        }

        //회원 정보 수정
        member.changeMemberInfo(modifyMemberInfoDto);
        memberRepository.save(member);
    }

    public void verifyIdAndEmail(String loginId, String email) {
        Member member = memberRepository.findByLoginIdAndEmail(loginId, email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }

    public void verifyPassword(String password, String email) {
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

    public Member socialRegisterNewMember(market.demo.dto.social.MemberRegistrationDto registrationDto) {
        // 현재 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomOAuth2User customOAuth2User = null;
        if (authentication != null && authentication.getPrincipal() instanceof CustomOAuth2User) {
            customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        }

        // CustomOAuth2User 정보가 있는 경우, DTO 업데이트
        if (customOAuth2User != null) {
            registrationDto.setProviderEmail(customOAuth2User.getEmail());
            registrationDto.setProvider(customOAuth2User.getProvider());
            registrationDto.setProviderId(customOAuth2User.getProviderId());
        }

        // 회원 생성 및 저장
        Member member = Member.createMemberWithProviderInfo(
                registrationDto.getMemberName(),
                registrationDto.getProviderEmail(), // 업데이트된 이메일 사용
//                customOAuth2User.getEmail(),
                registrationDto.getLoginId(),
                passwordEncoder.encode(registrationDto.getPassword()),
                registrationDto.getPhoneNumber(),
                registrationDto.getProvider(),      // 업데이트된 프로바이더 사용
//                customOAuth2User.getProvider(),
//                customOAuth2User.getProviderId()
                registrationDto.getProviderId()     // 업데이트된 프로바이더 ID 사용
        );
        memberRepository.save(member);

        return member;
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

        Member user = Member.createMember(
                memberDto.getLoginId(),
                memberDto.getMemberName(),
                memberDto.getEmail(),
                passwordEncoder.encode(memberDto.getPassword()),
                Collections.singleton(authority)
        );

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

    public MyPageDto getUserPageInfo(String loginId) {
        Member member = getMemberByLoginId(loginId);
        return new MyPageDto(member);
    }

    public List<CouponDto> getUserCoupons(String loginId) {
        Member member = getMemberByLoginId(loginId);
        return member.getCoupons().stream().map(CouponDto::new).collect(Collectors.toList());
    }

    public List<WishDto> getUserWishList(String loginId) {
        Member member = getMemberByLoginId(loginId);
        return member.getWishlist().getItems().stream().map(WishDto::new).collect(Collectors.toList());
    }
}
