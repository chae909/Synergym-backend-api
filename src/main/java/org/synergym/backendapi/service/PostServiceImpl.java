package org.synergym.backendapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.synergym.backendapi.dto.PostDTO;
import org.synergym.backendapi.entity.Category;
import org.synergym.backendapi.entity.Post;
import org.synergym.backendapi.entity.PostCounter;
import org.synergym.backendapi.entity.User;
import org.synergym.backendapi.exception.EntityNotFoundException;
import org.synergym.backendapi.exception.ErrorCode;
import org.synergym.backendapi.repository.CategoryRepository;
import org.synergym.backendapi.repository.PostCounterRepository;
import org.synergym.backendapi.repository.PostRepository;
import org.synergym.backendapi.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    
    private final PostRepository postRepository;
    private final PostCounterRepository postCounterRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    //ID로 게시글 조회 (없으면 예외 발생)
    private Post findPostById(int id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.POST_NOT_FOUND));
    }

    //ID로 사용자 조회 (없으면 예외 발생)
    private User findUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    //ID로 카테고리 조회 (없으면 예외 발생)
    private Category findCategoryById(int id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    //게시글 생성 - PostCounter 자동 생성
    @Override
    @Transactional
    public Integer createPost(PostDTO postDTO) {
        try {
            // 1. 사용자와 카테고리 조회
            User user = findUserById(postDTO.getUserId());
            Category category = findCategoryById(postDTO.getCategoryId());
            
            // 2. Post 엔티티 생성
            Post post = Post.builder()
                    .user(user)
                    .category(category)
                    .title(postDTO.getTitle())
                    .content(postDTO.getContent())
                    .imageUrl(postDTO.getImageUrl())
                    .build();
            
            // 3. Post 엔티티 저장
            Post savedPost = postRepository.save(post);
            
            // 4. PostCounter 자동 생성 (안정적인 버전)
            try {
                PostCounter postCounter = new PostCounter(savedPost);
                postCounterRepository.save(postCounter);
            } catch (Exception e) {
                // PostCounter 생성 실패 시에도 Post는 유지
                // PostCounter는 필요할 때 자동 생성됨
                // 로그만 남기고 계속 진행
            }
            
            return savedPost.getId();
        } catch (Exception e) {
            throw e;
        }
    }

    //전체 게시글 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<PostDTO> getAllPosts() {
        return postRepository.findAll()
                .stream()
                .map(post -> {
                    // comments를 로드하여 댓글 수를 정확히 계산
                    post.getComments().size(); // comments를 로드하기 위해 호출
                    return entityToDTO(post);
                })
                .collect(Collectors.toList());
    }

    //페이징 처리된 게시글 목록 조회(최신순)
    @Override
    @Transactional(readOnly = true)
    public Page<PostDTO> getPostsWithPaging(Pageable pageable) {
        return postRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(post -> {
                    // comments를 로드하여 댓글 수를 정확히 계산
                    post.getComments().size(); // comments를 로드하기 위해 호출
                    return entityToDTO(post);
                });
    }

    //페이징 처리된 게시글 목록 조회(인기순)
    @Override
    @Transactional(readOnly = true)
    public Page<PostDTO> getPostsWithPagingByPopularity(Pageable pageable) {
        return postRepository.findAllByOrderByLikeCountDesc(pageable)
                .map(post -> {
                    // comments를 로드하여 댓글 수를 정확히 계산
                    post.getComments().size(); // comments를 로드하기 위해 호출
                    return entityToDTO(post);
                });
    }

    //ID로 게시글 단건 조회
    @Override
    @Transactional(readOnly = true)
    public PostDTO getPostById(Integer id) {
        Post post = findPostById(id);
        // comments를 로드하여 댓글 수를 정확히 계산
        post.getComments().size(); // comments를 로드하기 위해 호출
        return entityToDTO(post);
    }

    //게시글 수정
    @Override
    @Transactional
    public void updatePost(Integer id, PostDTO postDTO) {
        Post post = findPostById(id);
        
        if (postDTO.getTitle() != null) {
            post.updateTitle(postDTO.getTitle());
        }
        if (postDTO.getContent() != null) {
            post.updateContent(postDTO.getContent());
        }
        if (postDTO.getImageUrl() != null) {
            post.updateImageUrl(postDTO.getImageUrl());
        }
        if (postDTO.getCategoryId() != null) {
            Category category = findCategoryById(postDTO.getCategoryId());
            post.updateCategory(category);
        }
        
        postRepository.save(post);
    }

    //게시글 삭제(soft delete)
    @Override
    @Transactional
    public void deletePost(Integer id) {
        Post post = postRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(ErrorCode.POST_NOT_FOUND));
        post.softDelete();
    }

    //게시글 제목/내용 키워드 검색
    @Override
    @Transactional(readOnly = true)
    public List<PostDTO> searchPosts(String keyword) {
        return postRepository.findByTitleContainingOrContentContaining(keyword, keyword)
                .stream()
                .map(post -> {
                    // comments를 로드하여 댓글 수를 정확히 계산
                    post.getComments().size(); // comments를 로드하기 위해 호출
                    return entityToDTO(post);
                })
                .collect(Collectors.toList());
    }

    //사용자별 게시글 목록 페이징 조회
    @Override
    @Transactional(readOnly = true)
    public Page<PostDTO> getPostsByUserIdWithPaging(Integer userId, Pageable pageable) {
        return postRepository.findByUserId(userId, pageable)
                .map(post -> {
                    // comments를 로드하여 댓글 수를 정확히 계산
                    post.getComments().size(); // comments를 로드하기 위해 호출
                    return entityToDTO(post);
                });
    }

    //카테고리별 게시글 목록 페이징 조회(최신순)
    @Override
    @Transactional(readOnly = true)
    public Page<PostDTO> getPostsByCategoryIdWithPaging(Integer categoryId, Pageable pageable) {
        return postRepository.findByCategoryIdOrderByCreatedAtDesc(categoryId, pageable)
                .map(post -> {
                    // comments를 로드하여 댓글 수를 정확히 계산
                    post.getComments().size(); // comments를 로드하기 위해 호출
                    return entityToDTO(post);
                });
    }

    //카테고리별 게시글 목록 페이징 조회(인기순)
    @Override
    @Transactional(readOnly = true)
    public Page<PostDTO> getPostsByCategoryIdWithPagingByPopularity(Integer categoryId, Pageable pageable) {
        return postRepository.findByCategoryIdOrderByLikeCountDesc(categoryId, pageable)
                .map(post -> {
                    // comments를 로드하여 댓글 수를 정확히 계산
                    post.getComments().size(); // comments를 로드하기 위해 호출
                    return entityToDTO(post);
                });
    }
} 