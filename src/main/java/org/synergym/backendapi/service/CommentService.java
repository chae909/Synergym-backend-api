package org.synergym.backendapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.synergym.backendapi.dto.CommentDTO;
import org.synergym.backendapi.entity.Comment;

import java.util.List;

public interface CommentService {

    // 댓글 생성
    Integer createComment(CommentDTO commentDTO);

    // 댓글 수정
    void updateComment(Integer id, CommentDTO commentDTO);

    // 댓글 삭제
    void deleteComment(Integer id);

    // ID로 댓글 조회
    CommentDTO getCommentById(Integer id);

    // 게시글별 댓글 조회 (페이징, 최신순)
    Page<CommentDTO> getCommentsByPostIdWithPaging(Integer postId, Pageable pageable);

    // 게시글별 댓글 조회 (페이징, 오래된순)
    Page<CommentDTO> getCommentsByPostIdWithPagingAsc(Integer postId, Pageable pageable);

    // 사용자별 댓글 조회 (페이징)
    Page<CommentDTO> getCommentsByUserIdWithPaging(Integer userId, Pageable pageable);

    // 게시글별 댓글 수 조회
    long getCommentCountByPostId(Integer postId);

    // 사용자별 댓글 수 조회
    long getCommentCountByUserId(Integer userId);

    // 내용으로 댓글 검색
    List<CommentDTO> searchComments(String keyword);

    // DTO -> Entity 변환
    default Comment DTOtoEntity(CommentDTO dto) {
        return Comment.builder()
                .content(dto.getContent())
                .build();
    }

    // Entity -> DTO 변환
    default CommentDTO entityToDTO(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .userId(comment.getUser().getId())
                .userName(comment.getUser().getName())
                .postId(comment.getPost().getId())
                .postTitle(comment.getPost().getTitle())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .useYn(comment.getUseYn())
                .build();
    }
} 