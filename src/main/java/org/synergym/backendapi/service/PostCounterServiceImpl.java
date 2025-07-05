package org.synergym.backendapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.synergym.backendapi.dto.PostCounterDTO;
import org.synergym.backendapi.entity.Post;
import org.synergym.backendapi.entity.PostCounter;
import org.synergym.backendapi.exception.EntityNotFoundException;
import org.synergym.backendapi.exception.ErrorCode;
import org.synergym.backendapi.repository.CommentRepository;
import org.synergym.backendapi.repository.PostCounterRepository;
import org.synergym.backendapi.repository.PostRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostCounterServiceImpl implements PostCounterService {
    
    private final PostCounterRepository postCounterRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public void incrementLikeCount(Integer postId) {
        try {
            PostCounter counter = getOrCreateCounter(postId);
            counter.incrementLikeCount();
            postCounterRepository.save(counter);
            log.debug("Incremented like count for postId: {}", postId);
        } catch (Exception e) {
            log.error("Error incrementing like count for postId: {}", postId, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void decrementLikeCount(Integer postId) {
        try {
            PostCounter counter = getOrCreateCounter(postId);
            counter.decrementLikeCount();
            postCounterRepository.save(counter);
            log.debug("Decremented like count for postId: {}", postId);
        } catch (Exception e) {
            log.error("Error decrementing like count for postId: {}", postId, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void incrementCommentCount(Integer postId) {
        try {
            PostCounter counter = getOrCreateCounter(postId);
            counter.incrementCommentCount();
            postCounterRepository.save(counter);
            log.debug("Incremented comment count for postId: {}", postId);
        } catch (Exception e) {
            log.error("Error incrementing comment count for postId: {}", postId, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void decrementCommentCount(Integer postId) {
        try {
            PostCounter counter = getOrCreateCounter(postId);
            counter.decrementCommentCount();
            postCounterRepository.save(counter);
            log.debug("Decremented comment count for postId: {}", postId);
        } catch (Exception e) {
            log.error("Error decrementing comment count for postId: {}", postId, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void incrementViewCount(Integer postId) {
        try {
            PostCounter counter = getOrCreateCounter(postId);
            counter.incrementViewCount();
            postCounterRepository.save(counter);
            log.debug("Incremented view count for postId: {}", postId);
        } catch (Exception e) {
            log.error("Error incrementing view count for postId: {}", postId, e);
            throw e;
        }
    }

    /**
     * 카운터 조회 또는 생성 (안정적인 버전)
     */
    private PostCounter getOrCreateCounter(Integer postId) {
        // 1. 기존 카운터 조회
        PostCounter counter = postCounterRepository.findByPostId(postId).orElse(null);
        
        if (counter != null) {
            return counter;
        }
        
        // 2. Post 존재 여부 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.error("Post not found for postId: {}", postId);
                    return new EntityNotFoundException(ErrorCode.POST_NOT_FOUND);
                });
        
        // 3. 새로운 PostCounter 생성
        try {
            counter = new PostCounter(post);
            counter = postCounterRepository.save(counter);
            log.info("Created new PostCounter for postId: {}", postId);
            return counter;
        } catch (Exception e) {
            log.error("Error creating PostCounter for postId: {}", postId, e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PostCounterDTO getCounter(Integer postId) {
        try {
            PostCounter counter = postCounterRepository.findByPostId(postId).orElse(null);
            PostCounterDTO dto = entityToDTO(counter);
            
            // PostCounter가 없는 경우 기본값으로 DTO 생성
            if (dto == null) {
                dto = PostCounterDTO.builder()
                        .postId(postId)
                        .likeCount(0)
                        .commentCount(0)
                        .viewCount(0)
                        .build();
            }
            
            return dto;
        } catch (Exception e) {
            log.error("Error getting counter for postId: {}", postId, e);
            // 에러 발생 시 기본값 반환
            return PostCounterDTO.builder()
                    .postId(postId)
                    .likeCount(0)
                    .commentCount(0)
                    .viewCount(0)
                    .build();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getLikeCount(Integer postId) {
        try {
            PostCounter counter = postCounterRepository.findByPostId(postId).orElse(null);
            return counter != null ? counter.getLikeCount() : 0;
        } catch (Exception e) {
            log.error("Error getting like count for postId: {}", postId, e);
            return 0;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getCommentCount(Integer postId) {
        try {
            PostCounter counter = postCounterRepository.findByPostId(postId).orElse(null);
            return counter != null ? counter.getCommentCount() : 0;
        } catch (Exception e) {
            log.error("Error getting comment count for postId: {}", postId, e);
            return 0;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getViewCount(Integer postId) {
        try {
            PostCounter counter = postCounterRepository.findByPostId(postId).orElse(null);
            return counter != null ? counter.getViewCount() : 0;
        } catch (Exception e) {
            log.error("Error getting view count for postId: {}", postId, e);
            return 0;
        }
    }

    /**
     * 기존 댓글들의 commentCount를 PostCounter에 반영 (마이그레이션용)
     */
    @Transactional
    public void syncCommentCounts() {
        try {
            // 모든 Post 조회
            List<Post> posts = postRepository.findAll();
            
            for (Post post : posts) {
                // 실제 댓글 수 계산 (Post 엔티티의 comments 관계 사용)
                int actualCommentCount = post.getComments().size();
                
                // PostCounter 조회 또는 생성
                PostCounter counter = postCounterRepository.findByPostId(post.getId()).orElse(null);
                if (counter == null) {
                    counter = new PostCounter(post);
                }
                
                // commentCount 동기화
                counter.setCommentCount(actualCommentCount);
                postCounterRepository.save(counter);
                
                log.info("Synced comment count for postId: {} - count: {}", post.getId(), actualCommentCount);
            }
        } catch (Exception e) {
            log.error("Error syncing comment counts", e);
            throw e;
        }
    }
} 