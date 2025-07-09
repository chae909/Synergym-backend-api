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
    private final PostCounterService postCounterService;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final NotificationService notificationService;
    
    //ID로 사용자 조회 (없으면 예외 발생)
    private User findUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    //ID로 게시글 조회 (없으면 예외 발생)
    private Post findPostById(int id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.POST_NOT_FOUND));
    }

    //게시글 좋아요 생성 - 중복 방지, 알림 생성, PostCounter 동기화
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
        
        // 게시글 좋아요 알림 생성
        notificationService.createPostLikeNotification(postLikeDTO.getPostId(), postLikeDTO.getUserId());

        postLikeRepository.save(postLike);
        
        // PostCounter의 likeCount 업데이트
        postCounterService.incrementLikeCount(postLikeDTO.getPostId());
    }

    //게시글 좋아요 삭제 - PostCounter 동기화
    @Override
    @Transactional
    public void deletePostLike(Integer userId, Integer postId) {
        PostLike postLike = postLikeRepository.findByUserIdAndPostId(userId, postId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.POST_LIKE_NOT_FOUND));
        
        postLikeRepository.delete(postLike);
        
        // PostCounter의 likeCount 업데이트
        postCounterService.decrementLikeCount(postId);
    }

    //사용자가 누른 좋아요 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<PostLikeDTO> getPostLikesByUserId(Integer userId) {
        return postLikeRepository.findByUserId(userId)
                .stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    //게시글별 좋아요 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<PostLikeDTO> getPostLikesByPostId(Integer postId) {
        return postLikeRepository.findByPostId(postId)
                .stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    //특정 사용자가 특정 게시글에 좋아요를 눌렀는지 확인
    @Override
    @Transactional(readOnly = true)
    public boolean existsByUserIdAndPostId(Integer userId, Integer postId) {
        return postLikeRepository.findByUserIdAndPostId(userId, postId).isPresent();
    }

    //게시글별 좋아요 수 조회
    @Override
    @Transactional(readOnly = true)
    public long getLikeCountByPostId(Integer postId) {
        return postLikeRepository.countByPostId(postId);
    }

    //사용자별 좋아요 수 조회
    @Override
    @Transactional(readOnly = true)
    public long getLikeCountByUserId(Integer userId) {
        return postLikeRepository.countByUserId(userId);
    }
} 