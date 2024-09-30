package com.example.echo.domain.member.service;

import com.example.echo.domain.member.dto.MemberDto;
import com.example.echo.domain.member.entity.Member;
import com.example.echo.domain.member.repository.MemberRepository;
import com.example.echo.domain.member.dto.request.ProfileImageUpdateRequest;
import com.example.echo.domain.member.dto.response.ProfileImageUpdateResponse;
import com.example.echo.global.util.UploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final UploadUtil uploadUtil;

    //회원 가입
    public Member signup(Member member) {
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        return memberRepository.save(member);
    }

    //로그인 시 사용자의 정보를 조회
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Optional<Member> memberOptional = memberRepository.findByUserId(userId);
        if (memberOptional.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
        Member member = memberOptional.get();

        // UserDetails 반환 (username, password, 권한)
        return User.builder()
                .username(member.getUserId())
                .password(member.getPassword())
                .roles(member.getRole().name())
                .build();
    }


    // 회원 등록
    @Transactional
    public MemberDto createMember(MemberDto memberDto) {
        Member savedMember = memberRepository.save(memberDto.toEntity());
        return MemberDto.of(savedMember);
    }

    //회원 조회
    public MemberDto getMember(Long memberId){  // Long memberId로 변경
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new RuntimeException("회원정보를 찾을수 없습니다."));
        return MemberDto.of(member);
    }

    //전체 회원 조회
    public List<MemberDto> getAllMembers(){
        return memberRepository.findAll().stream()
                .map(MemberDto::of)
                .collect(Collectors.toList());
    }

    //회원 정보 수정
    @Transactional
    public MemberDto updateMember(Long memberId, MemberDto memberDto){ // Long memberId로 변경
        Member member = memberRepository.findById(memberId)
            .orElseThrow(()-> new RuntimeException("회원정보를 찾을 수 없습니다."));

        member.setUserId(memberDto.getUserId()); // userId로 변경
        member.setName(memberDto.getName());
        member.setEmail(memberDto.getEmail());
        member.setPhone(memberDto.getPhone());
        member.setAvatarImage(memberDto.getAvatarImage());
        member.setRole(memberDto.getRole());

        return MemberDto.of(memberRepository.save(member));
    }


    //id로 회원 삭제
    @Transactional
    public void deleteMember(Long memberId){ // Long memberId로 변경
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new RuntimeException("회원 정보를 찾을 수 없습니다."));
        memberRepository.delete(member);
    }

    //UserId로 유저 찾기
    public MemberDto findByUserId(String userId){
        Optional<Member> memberOpt = memberRepository.findByUserId(userId);

        if (memberOpt.isPresent()){
            return MemberDto.of(memberOpt.get());
        }else{
            throw new RuntimeException("회원이 찾을 수 없습니다 : "+userId);
        }
    }

    // 프로필 사진 조회
    public String getAvatar(Long id) {
        Member member = findMemberById(id);
        return member.getAvatarImage(); // URL 반환
    }

    // 프로필 사진 업데이트
    @Transactional
    public ProfileImageUpdateResponse updateAvatar(Long id, ProfileImageUpdateRequest requestDto) {
        Member member = findMemberById(id);

        // 파일 업로드 후 경로 받기
        String avatarUrl = uploadUtil.upload(requestDto.getAvatarImage());
        member.setAvatarImage(avatarUrl); // 경로 업데이트

        // 회원 정보를 저장하고 응답 DTO 생성
        Member updatedMember = memberRepository.save(member);
        return ProfileImageUpdateResponse.from(updatedMember);
    }

    // 공통 메서드: 회원 ID로 회원 조회
    private Member findMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("회원정보를 찾을 수 없습니다."));
    }
}
