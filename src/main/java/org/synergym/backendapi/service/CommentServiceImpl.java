package org.synergym.backendapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.synergym.backendapi.dto.CommentDTO;
import org.synergym.backendapi.entity.Comment;
import org.synergym.backendapi.entity.Post;
import org.synergym.backendapi.entity.User;
import org.synergym.backendapi.exception.EntityNotFoundException;
import org.synergym.backendapi.exception.ErrorCode;
import org.synergym.backendapi.repository.CommentRepository;
import org.synergym.backendapi.repository.PostRepository;
import org.synergym.backendapi.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

//게시글 댓글의 생성, 수정, 삭제, 조회 등 비즈니스 로직 처리
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostCounterService postCounterService;
    private final NotificationService notificationService;

    // ID로 댓글 조회 (없으면 예외 발생)
    private Comment findCommentById(int id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.COMMENT_NOT_FOUND));
    }

    // ID로 사용자 조회 (없으면 예외 발생)
    private User findUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    // ID로 게시글 조회 (없으면 예외 발생)
    private Post findPostById(int id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.POST_NOT_FOUND));
    }

    //댓글 생성- 댓글 저장, 알림 생성, PostCounter의 댓글 수 증가
    @Override
    @Transactional
    public Integer createComment(CommentDTO commentDTO) {
        User user = findUserById(commentDTO.getUserId());
        Post post = findPostById(commentDTO.getPostId());
        
        Comment comment = Comment.builder()
                .user(user)
                .post(post)
                .content(commentDTO.getContent())
                .build();
        
        Comment savedComment = commentRepository.save(comment);

         // 댓글 작성 알림 생성
        notificationService.createCommentNotification(commentDTO.getPostId(), commentDTO.getUserId());
        
        // PostCounter의 댓글 수 증가
        try {
            postCounterService.incrementCommentCount(post.getId());
        } catch (Exception e) {
            // PostCounter 업데이트 실패 시에도 댓글은 생성됨
            // 로그만 남기고 계속 진행
        }
        
        return savedComment.getId();
    }

    //댓글 내용 수정
    @Override
    @Transactional
    public void updateComment(Integer id, CommentDTO commentDTO) {
        Comment comment = findCommentById(id);
        
        if (commentDTO.getContent() != null) {
            comment.updateContent(commentDTO.getContent());
        }
        
        commentRepository.save(comment);
    }

    //댓글 삭제- 댓글 삭제, PostCounter의 댓글 수 감소
    @Override
    @Transactional
    public void deleteComment(Integer id) {
        Comment comment = findCommentById(id);
        Integer postId = comment.getPost().getId();
        
        commentRepository.delete(comment);
        
        // PostCounter의 댓글 수 감소
        try {
            postCounterService.decrementCommentCount(postId);
        } catch (Exception e) {
            // PostCounter 업데이트 실패 시에도 댓글은 삭제됨
            // 로그만 남기고 계속 진행
        }
    }

    //ID로 댓글 단건 조회
    @Override
    @Transactional(readOnly = true)
    public CommentDTO getCommentById(Integer id) {
        Comment comment = findCommentById(id);
        return entityToDTO(comment);
    }

    //게시글별 댓글 목록(최신순) 페이징 조회
    @Override
    @Transactional(readOnly = true)
    public Page<CommentDTO> getCommentsByPostIdWithPaging(Integer postId, Pageable pageable) {
        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId, pageable)
                .map(this::entityToDTO);
    }

    //게시글별 댓글 목록(오래된순) 페이징 조회
    @Override
    @Transactional(readOnly = true)
    public Page<CommentDTO> getCommentsByPostIdWithPagingAsc(Integer postId, Pageable pageable) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId, pageable)
                .map(this::entityToDTO);
    }

    //사용자별 댓글 목록(최신순) 페이징 조회
    @Override
    @Transactional(readOnly = true)
    public Page<CommentDTO> getCommentsByUserIdWithPaging(Integer userId, Pageable pageable) {
        return commentRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::entityToDTO);
    }

    //게시글별 댓글 수 조회
    @Override
    @Transactional(readOnly = true)
    public long getCommentCountByPostId(Integer postId) {
        return commentRepository.countByPostId(postId);
    }

    //사용자별 댓글 수 조회
    @Override
    @Transactional(readOnly = true)
    public long getCommentCountByUserId(Integer userId) {
        return commentRepository.countByUserId(userId);
    }

    //댓글 내용 키워드 검색
    @Override
    @Transactional(readOnly = true)
    public List<CommentDTO> searchComments(String keyword) {
        return commentRepository.findByContentContaining(keyword)
                .stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }
} 