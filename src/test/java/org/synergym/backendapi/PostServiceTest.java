package org.synergym.backendapi;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.synergym.backendapi.dto.PostDTO;
import org.synergym.backendapi.service.PostService;

import java.util.List;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PostServiceTest {

    @Autowired
    private PostService postService;

    private static Integer testPostId;
    private static Integer testUserId = 46; // 테스트용 사용자 ID
    private static Integer testCategoryId = 1; // 테스트용 카테고리 ID

    @BeforeAll
    static void setUp() {
        System.out.println("=== PostService 테스트 시작 ===");
    }

    @Test
    @Order(1)
    @DisplayName("게시글 생성 테스트")
    void createPost() {
        System.out.println("--- 게시글 생성 테스트 ---");
        
        // 첫 번째 게시글 생성
        PostDTO newPostDTO1 = PostDTO.builder()
                .userId(testUserId)
                .categoryId(testCategoryId)
                .title("첫 번째 테스트 게시글")
                .content("첫 번째 테스트 게시글 내용입니다.")
                .imageUrl("https://example.com/test-image1.jpg")
                .build();

        Integer postId1 = postService.createPost(newPostDTO1);
        System.out.println("생성된 게시글 1 ID: " + postId1);
        
        // 두 번째 게시글 생성
        PostDTO newPostDTO2 = PostDTO.builder()
                .userId(testUserId)
                .categoryId(testCategoryId)
                .title("두 번째 테스트 게시글")
                .content("두 번째 테스트 게시글 내용입니다.")
                .imageUrl("https://example.com/test-image2.jpg")
                .build();

        Integer postId2 = postService.createPost(newPostDTO2);
        System.out.println("생성된 게시글 2 ID: " + postId2);
        
        // 세 번째 게시글 생성
        PostDTO newPostDTO3 = PostDTO.builder()
                .userId(testUserId)
                .categoryId(testCategoryId)
                .title("세 번째 테스트 게시글")
                .content("세 번째 테스트 게시글 내용입니다.")
                .imageUrl("https://example.com/test-image3.jpg")
                .build();

        Integer postId3 = postService.createPost(newPostDTO3);
        System.out.println("생성된 게시글 3 ID: " + postId3);
        
        // 첫 번째 게시글을 메인 테스트용으로 사용
        testPostId = postId1;
        
        // 생성된 게시글들 조회하여 확인
        PostDTO createdPost1 = postService.getPostById(postId1);
        PostDTO createdPost2 = postService.getPostById(postId2);
        PostDTO createdPost3 = postService.getPostById(postId3);
        
        System.out.println("=== 생성된 게시글 정보 ===");
        System.out.println("게시글 1: " + createdPost1.getTitle() + " (작성자: " + createdPost1.getUserName() + ")");
        System.out.println("게시글 2: " + createdPost2.getTitle() + " (작성자: " + createdPost2.getUserName() + ")");
        System.out.println("게시글 3: " + createdPost3.getTitle() + " (작성자: " + createdPost3.getUserName() + ")");
        System.out.println("총 생성된 게시글 수: 3개");
    }

    @Test
    @Order(2)
    @DisplayName("게시글 ID 조회 테스트")
    void getPostById() {
        System.out.println("--- 게시글 ID 조회 테스트 ---");
        
        PostDTO post = postService.getPostById(testPostId);
        System.out.println("조회된 게시글: " + post.getTitle());
        System.out.println("내용: " + post.getContent());
        System.out.println("생성일: " + post.getCreatedAt());
    }

    @Test
    @Order(3)
    @DisplayName("전체 게시글 조회 테스트")
    void getAllPosts() {
        System.out.println("--- 전체 게시글 조회 테스트 ---");
        
        List<PostDTO> allPosts = postService.getAllPosts();
        System.out.println("전체 게시글 개수: " + allPosts.size());
        
        allPosts.forEach(post -> 
            System.out.println("- " + post.getTitle() + " (작성자: " + post.getUserName() + ")")
        );
    }

    @Test
    @Order(4)
    @DisplayName("페이징 게시글 조회 테스트 (최신순)")
    void getPostsWithPaging() {
        System.out.println("--- 페이징 게시글 조회 테스트 (최신순) ---");
        
        Pageable pageable = PageRequest.of(0, 5);
        var posts = postService.getPostsWithPaging(pageable);
        
        System.out.println("페이지 정보:");
        System.out.println("- 전체 게시글 수: " + posts.getTotalElements());
        System.out.println("- 전체 페이지 수: " + posts.getTotalPages());
        System.out.println("- 현재 페이지: " + (posts.getNumber() + 1));
        System.out.println("- 페이지당 게시글 수: " + posts.getSize());
        
        posts.getContent().forEach(post -> 
            System.out.println("- " + post.getTitle() + " (생성일: " + post.getCreatedAt() + ")")
        );
    }

    @Test
    @Order(5)
    @DisplayName("페이징 게시글 조회 테스트 (인기순)")
    void getPostsWithPagingByPopularity() {
        System.out.println("--- 페이징 게시글 조회 테스트 (인기순) ---");
        
        Pageable pageable = PageRequest.of(0, 5);
        var posts = postService.getPostsWithPagingByPopularity(pageable);
        
        System.out.println("인기순 게시글:");
        posts.getContent().forEach(post -> 
            System.out.println("- " + post.getTitle() + " (좋아요: " + post.getLikeCount() + ")")
        );
    }

    @Test
    @Order(6)
    @DisplayName("카테고리별 게시글 조회 테스트 (최신순)")
    void getPostsByCategoryIdWithPaging() {
        System.out.println("--- 카테고리별 게시글 조회 테스트 (최신순) ---");
        
        Pageable pageable = PageRequest.of(0, 5);
        var posts = postService.getPostsByCategoryIdWithPaging(testCategoryId, pageable);
        
        System.out.println("카테고리 " + testCategoryId + "의 최신 게시글:");
        posts.getContent().forEach(post -> 
            System.out.println("- " + post.getTitle() + " (생성일: " + post.getCreatedAt() + ")")
        );
    }

    @Test
    @Order(7)
    @DisplayName("카테고리별 게시글 조회 테스트 (인기순)")
    void getPostsByCategoryIdWithPagingByPopularity() {
        System.out.println("--- 카테고리별 게시글 조회 테스트 (인기순) ---");
        
        Pageable pageable = PageRequest.of(0, 5);
        var posts = postService.getPostsByCategoryIdWithPagingByPopularity(testCategoryId, pageable);
        
        System.out.println("카테고리 " + testCategoryId + "의 인기 게시글:");
        posts.getContent().forEach(post -> 
            System.out.println("- " + post.getTitle() + " (좋아요: " + post.getLikeCount() + ")")
        );
    }

    @Test
    @Order(8)
    @DisplayName("사용자별 게시글 조회 테스트")
    void getPostsByUserIdWithPaging() {
        System.out.println("--- 사용자별 게시글 조회 테스트 ---");
        
        Pageable pageable = PageRequest.of(0, 5);
        var posts = postService.getPostsByUserIdWithPaging(testUserId, pageable);
        
        System.out.println("사용자 " + testUserId + "의 게시글:");
        posts.getContent().forEach(post -> 
            System.out.println("- " + post.getTitle() + " (카테고리: " + post.getCategoryName() + ")")
        );
    }

    @Test
    @Order(9)
    @DisplayName("게시글 검색 테스트")
    void searchPosts() {
        System.out.println("--- 게시글 검색 테스트 ---");
        
        List<PostDTO> searchResults = postService.searchPosts("테스트");
        System.out.println("'테스트' 검색 결과 개수: " + searchResults.size());
        
        searchResults.forEach(post -> 
            System.out.println("- " + post.getTitle() + " (내용: " + post.getContent().substring(0, Math.min(20, post.getContent().length())) + "...)")
        );
    }

    @Test
    @Order(10)
    @DisplayName("게시글 수정 테스트")
    void updatePost() {
        System.out.println("--- 게시글 수정 테스트 ---");
        
        PostDTO updateInfo = PostDTO.builder()
                .title("수정된 게시글 제목")
                .content("수정된 게시글 내용입니다.")
                .build();

        postService.updatePost(testPostId, updateInfo);
        System.out.println("게시글 수정 완료");
        
        // 수정된 게시글 조회
        PostDTO updatedPost = postService.getPostById(testPostId);
        System.out.println("수정된 제목: " + updatedPost.getTitle());
        System.out.println("수정된 내용: " + updatedPost.getContent());
        System.out.println("수정일: " + updatedPost.getUpdatedAt());
    }

    @Test
    @Order(11)
    @DisplayName("게시글 삭제 테스트")
    void deletePost() {
        System.out.println("--- 게시글 삭제 테스트 ---");
        
        postService.deletePost(testPostId);
        System.out.println("게시글 삭제 완료");
        
        // 삭제 확인
        List<PostDTO> allPosts = postService.getAllPosts();
        System.out.println("삭제 후 전체 게시글 개수: " + allPosts.size());
    }

    @AfterAll
    static void tearDown() {
        System.out.println("=== PostService 테스트 완료 ===");
    }
} 