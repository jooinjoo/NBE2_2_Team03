package com.example.echo.domain.inquiry.service;

import com.example.echo.domain.inquiry.dto.request.InquiryPageRequestDTO;
import com.example.echo.domain.inquiry.dto.request.InquiryRequestDTO;
import com.example.echo.domain.inquiry.dto.response.InquiryResponseDTO;
import com.example.echo.domain.inquiry.entity.Inquiry;
import com.example.echo.domain.inquiry.repository.InquiryRepository;
import com.example.echo.domain.member.dto.MemberDto;
import com.example.echo.domain.member.entity.Member;
import com.example.echo.domain.member.entity.Role;
import com.example.echo.domain.member.service.MemberService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final MemberService memberService;
    private final InquiryRepository inquiryRepository;

    @Transactional
    public InquiryResponseDTO createInquiry(InquiryRequestDTO inquiryRequestDTO) {
        Member foundMember = memberService.findMemberById(inquiryRequestDTO.getMemberId());    // memberId로 Member 엔티티를 조회
        Inquiry createdInquiry = inquiryRequestDTO.toEntity(foundMember);   // 1:1 문의 생성
        Inquiry savedInquiry = inquiryRepository.save(createdInquiry);
        return InquiryResponseDTO.from(savedInquiry);
    }

    // 모든 회원 1:1 문의 단건 조회
    public InquiryResponseDTO getInquiryById(Long inquiryId) {
        Inquiry foundInquiry = findInquiryById(inquiryId);
        return InquiryResponseDTO.from(foundInquiry);
    }

    // ADMIN/USER 회원 종류에 따른 1:1 문의 전체 리스트 조회
    public Page<InquiryResponseDTO> getInquiriesByMemberRole(Long memberId, InquiryPageRequestDTO inquiryPageRequestDTO) {
        // memberId에 따라 해당 다르게 조회
        MemberDto foundMember = memberService.getMember(memberId);
        if (foundMember.getRole() == Role.ADMIN) {
            return findAllForAdmin(inquiryPageRequestDTO.getPageable());    // ADMIN 모든 문의 조회
        } else {
            return findAllForUser(memberId, inquiryPageRequestDTO.getPageable());     // USER 개인 모든 문의 조회
        }
    }

    // 문의 ID로 문의 조회
    private Inquiry findInquiryById(Long inquiryId) {
        return inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new RuntimeException("1:1 문의 정보를 찾을 수 없습니다."));
    }

    // ADMIN 모든 문의 조회
    private Page<InquiryResponseDTO> findAllForAdmin(Pageable pageable) {
        return inquiryRepository.findAllInquiriesAdmin(pageable);
    }

    // USER 본인 문의 조회
    private Page<InquiryResponseDTO> findAllForUser(Long memberId, Pageable pageable) {
        return inquiryRepository.findAllInquiriesUser(memberId, pageable);
    }
}