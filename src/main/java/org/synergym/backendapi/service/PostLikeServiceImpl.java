package org.synergym.backendapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.synergym.backendapi.dto.PostLikeDTO;
import org.synergym.backendapi.entity.Post;
import org.synergym.backendapi.entity.PostLike;
import org.synergym.backendapi.entity.User;
import org.synergym.backendapi.exception.EntityNotFoundException;
import org.synergym.backendapi.exception.ErrorCode;
import org.synergym.backendapi.repository.PostLikeRepository;
import org.synergym.backendapi.repository.PostRepository;
import org.synergym.backendapi.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostLikeServiceImpl implements PostLikeService {
    
    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

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
    public void createPostLike(PostLikeDTO postLikeDTO) {
        // 이미 좋아요를 눌렀는지 확인
        if (postLikeRepository.findByUserIdAndPostId(postLikeDTO.getUserId(), postLikeDTO.getPostId()).isPresent()) {
            throw new IllegalArgumentException("이미 좋아요를 누른 게시글입니다.");
        }

        User user = findUserById(postLikeDTO.getUserId());
        Post post = findPostById(postLikeDTO.getPostId());
        
        PostLike postLike = PostLike.builder()
                .user(user)
                .post(post)
                .build();
        
        postLikeRepository.save(postLike);
        
        // Post 엔티티의 likeCount 업데이트
        post.incrementLikeCount();
        postRepository.save(post);
    }

    @Override
    @Transactional
    public void deletePostLike(Integer userId, Integer postId) {
        PostLike postLike = postLikeRepository.findByUserIdAndPostId(userId, postId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.POST_LIKE_NOT_FOUND));
        
        postLikeRepository.delete(postLike);
        
        // Post 엔티티의 likeCount 업데이트
        Post post = findPostById(postId);
        post.decrementLikeCount();
        postRepository.save(post);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostLikeDTO> getPostLikesByUserId(Integer userId) {
        return postLikeRepository.findByUserId(userId)
                .stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostLikeDTO> getPostLikesByPostId(Integer postId) {
        return postLikeRepository.findByPostId(postId)
                .stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUserIdAndPostId(Integer userId, Integer postId) {
        return postLikeRepository.findByUserIdAndPostId(userId, postId).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public long getLikeCountByPostId(Integer postId) {
        return postLikeRepository.countByPostId(postId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getLikeCountByUserId(Integer userId) {
        return postLikeRepository.countByUserId(userId);
    }
} 