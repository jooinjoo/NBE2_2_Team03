package com.example.echo.domain.inquiry.dto.response;

import com.example.echo.domain.inquiry.entity.Inquiry;
import com.example.echo.domain.inquiry.entity.InquiryCategory;
import com.example.echo.domain.inquiry.entity.InquiryStatus;
import com.example.echo.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryResponseDTO {

    private Long inquiryId;
    private Long memberId;
    private InquiryCategory inquiryCategory;
    private String inquiryTitle;
    private String inquiryContent;
    private LocalDateTime createdDate;
    private String replyContent;
    private InquiryStatus inquiryStatus;
    private LocalDateTime repliedDate;

    public static InquiryResponseDTO from(Inquiry inquiry) {
        return InquiryResponseDTO.builder()
                .inquiryId(inquiry.getInquiryId())
                .memberId(inquiry.getMember().getMemberId())
                .inquiryCategory(inquiry.getInquiryCategory())
                .inquiryTitle(inquiry.getInquiryTitle())
                .inquiryContent(inquiry.getInquiryContent())
                .createdDate(inquiry.getCreatedDate())
                .replyContent(inquiry.getReplyContent())
                .inquiryStatus(inquiry.getInquiryStatus())
                .repliedDate(inquiry.getRepliedDate())
                .build();
    }
}
