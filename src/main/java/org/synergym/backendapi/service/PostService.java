package org.synergym.backendapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.synergym.backendapi.dto.PostDTO;
import org.synergym.backendapi.entity.Post;

import java.util.List;

public interface PostService {

    // 게시글 생성
    Integer createPost(PostDTO postDTO);

    // 모든 게시글 조회
    List<PostDTO> getAllPosts();

    // 페이징된 게시글 조회 (최신순)
    Page<PostDTO> getPostsWithPaging(Pageable pageable);

    // 페이징된 게시글 조회 (인기순)
    Page<PostDTO> getPostsWithPagingByPopularity(Pageable pageable);

    // ID로 게시글 조회
    PostDTO getPostById(Integer id);

    // 게시글 수정
    void updatePost(Integer id, PostDTO postDTO);

    // 게시글 삭제
    void deletePost(Integer id);

    // 제목 또는 내용으로 게시글 검색
    List<PostDTO> searchPosts(String keyword);

    // 사용자별 게시글 조회 (페이징)
    Page<PostDTO> getPostsByUserIdWithPaging(Integer userId, Pageable pageable);

    // 카테고리별 게시글 조회 (페이징, 최신순)
    Page<PostDTO> getPostsByCategoryIdWithPaging(Integer categoryId, Pageable pageable);

    // 카테고리별 게시글 조회 (페이징, 인기순)
    Page<PostDTO> getPostsByCategoryIdWithPagingByPopularity(Integer categoryId, Pageable pageable);

    // DTO -> Entity 변환
    default Post DTOtoEntity(PostDTO dto) {
        return Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .imageUrl(dto.getImageUrl())
                .build();
    }

    // Entity -> DTO 변환
    default PostDTO entityToDTO(Post post) {
        return PostDTO.builder()
                .id(post.getId())
                .userId(post.getUser().getId())
                .userName(post.getUser().getName())
                .categoryId(post.getCategory().getId())
                .categoryName(post.getCategory().getName())
                .title(post.getTitle())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .likeCount(post.getLikeCount())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .useYn(post.getUseYn())
                .build();
    }
} 