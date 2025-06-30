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

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    private Comment findCommentById(int id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.COMMENT_NOT_FOUND));
    }

    private User findUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    private Post findPostById(int id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.POST_NOT_FOUND));
    }

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
        return savedComment.getId();
    }

    @Override
    @Transactional
    public void updateComment(Integer id, CommentDTO commentDTO) {
        Comment comment = findCommentById(id);
        
        if (commentDTO.getContent() != null) {
            comment.updateContent(commentDTO.getContent());
        }
        
        commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Integer id) {
        Comment comment = findCommentById(id);
        commentRepository.delete(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDTO getCommentById(Integer id) {
        Comment comment = findCommentById(id);
        return entityToDTO(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentDTO> getCommentsByPostIdWithPaging(Integer postId, Pageable pageable) {
        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId, pageable)
                .map(this::entityToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentDTO> getCommentsByPostIdWithPagingAsc(Integer postId, Pageable pageable) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId, pageable)
                .map(this::entityToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentDTO> getCommentsByUserIdWithPaging(Integer userId, Pageable pageable) {
        return commentRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::entityToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public long getCommentCountByPostId(Integer postId) {
        return commentRepository.countByPostId(postId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getCommentCountByUserId(Integer userId) {
        return commentRepository.countByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDTO> searchComments(String keyword) {
        return commentRepository.findByContentContaining(keyword)
                .stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }
} 