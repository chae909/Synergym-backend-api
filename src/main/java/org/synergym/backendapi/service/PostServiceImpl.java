package org.synergym.backendapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.synergym.backendapi.dto.PostDTO;
import org.synergym.backendapi.entity.Category;
import org.synergym.backendapi.entity.Post;
import org.synergym.backendapi.entity.User;
import org.synergym.backendapi.exception.EntityNotFoundException;
import org.synergym.backendapi.exception.ErrorCode;
import org.synergym.backendapi.repository.CategoryRepository;
import org.synergym.backendapi.repository.PostRepository;
import org.synergym.backendapi.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    private Post findPostById(int id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.POST_NOT_FOUND));
    }

    private User findUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    private Category findCategoryById(int id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    @Override
    @Transactional
    public Integer createPost(PostDTO postDTO) {
        User user = findUserById(postDTO.getUserId());
        Category category = findCategoryById(postDTO.getCategoryId());
        
        Post post = Post.builder()
                .user(user)
                .category(category)
                .title(postDTO.getTitle())
                .content(postDTO.getContent())
                .imageUrl(postDTO.getImageUrl())
                .build();
        
        Post savedPost = postRepository.save(post);
        return savedPost.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostDTO> getAllPosts() {
        return postRepository.findAll()
                .stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostDTO> getPostsWithPaging(Pageable pageable) {
        return postRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::entityToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostDTO> getPostsWithPagingByPopularity(Pageable pageable) {
        return postRepository.findAllByOrderByLikeCountDesc(pageable)
                .map(this::entityToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public PostDTO getPostById(Integer id) {
        Post post = findPostById(id);
        return entityToDTO(post);
    }

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

    @Override
    @Transactional
    public void deletePost(Integer id) {
        Post post = findPostById(id);
        postRepository.delete(post);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostDTO> searchPosts(String keyword) {
        return postRepository.findByTitleContainingOrContentContaining(keyword, keyword)
                .stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostDTO> getPostsByUserIdWithPaging(Integer userId, Pageable pageable) {
        return postRepository.findByUserId(userId, pageable)
                .map(this::entityToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostDTO> getPostsByCategoryIdWithPaging(Integer categoryId, Pageable pageable) {
        return postRepository.findByCategoryIdOrderByCreatedAtDesc(categoryId, pageable)
                .map(this::entityToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostDTO> getPostsByCategoryIdWithPagingByPopularity(Integer categoryId, Pageable pageable) {
        return postRepository.findByCategoryIdOrderByLikeCountDesc(categoryId, pageable)
                .map(this::entityToDTO);
    }
} 