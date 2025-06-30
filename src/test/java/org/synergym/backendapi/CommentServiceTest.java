package org.synergym.backendapi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.synergym.backendapi.dto.CommentDTO;
import org.synergym.backendapi.entity.Category;
import org.synergym.backendapi.entity.Post;
import org.synergym.backendapi.entity.User;
import org.synergym.backendapi.exception.EntityNotFoundException;
import org.synergym.backendapi.repository.CategoryRepository;
import org.synergym.backendapi.repository.PostRepository;
import org.synergym.backendapi.repository.UserRepository;
import org.synergym.backendapi.service.CommentService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Integer testUserId1;
    private Integer testUserId2;
    private Integer testPostId1;
    private Integer testPostId2;

    @BeforeEach
    void setUp() {
        // @Transactional로 인해 테스트 후 자동 롤백되므로 기존 데이터 삭제 불필요
        // 테스트용 고유 데이터만 생성하여 기존 데이터와 격리
        
        // 테스트 데이터 설정
        setupTestData();
    }
    
    private void setupTestData() {
        // 테스트용 고유 이메일로 사용자 생성 (기존 데이터와 격리)
        String uniqueEmail1 = "test_comment_" + System.currentTimeMillis() + "_1@example.com";
        String uniqueEmail2 = "test_comment_" + System.currentTimeMillis() + "_2@example.com";
        
        User testUser1 = User.builder()
                .email(uniqueEmail1)
                .password("password123")
                .name("테스트 사용자1")
                .build();
        testUser1 = userRepository.save(testUser1);
        testUserId1 = testUser1.getId();
        
        User testUser2 = User.builder()
                .email(uniqueEmail2)
                .password("password123")
                .name("테스트 사용자2")
                .build();
        testUser2 = userRepository.save(testUser2);
        testUserId2 = testUser2.getId();
        
        // 테스트용 고유 카테고리 생성
        String uniqueCategoryName = "테스트 카테고리_" + System.currentTimeMillis();
        Category testCategory = Category.builder()
                .name(uniqueCategoryName)
                .build();
        testCategory = categoryRepository.save(testCategory);
        
        // 테스트용 고유 게시글 생성
        Post testPost1 = Post.builder()
                .user(testUser1)
                .category(testCategory)
                .title("테스트 게시글1_" + System.currentTimeMillis())
                .content("테스트 내용1")
                .build();
        testPost1 = postRepository.save(testPost1);
        testPostId1 = testPost1.getId();

        Post testPost2 = Post.builder()
                .user(testUser2)
                .category(testCategory)
                .title("테스트 게시글2_" + System.currentTimeMillis())
                .content("테스트 내용2")
                .build();
        testPost2 = postRepository.save(testPost2);
        testPostId2 = testPost2.getId();
    }

    @Test
    @DisplayName("댓글 생성 테스트")
    void createComment() {
        // given
        CommentDTO commentDTO = CommentDTO.builder()
                .userId(testUserId1)
                .postId(testPostId2)
                .content("테스트 댓글입니다.")
                .build();

        // when
        Integer commentId = commentService.createComment(commentDTO);

        // then
        assertThat(commentId).isNotNull();
        assertThat(commentService.getCommentCountByPostId(testPostId2)).isEqualTo(1);
    }

    @Test
    @DisplayName("댓글 수정 테스트")
    void updateComment() {
        // given
        CommentDTO commentDTO = CommentDTO.builder()
                .userId(testUserId1)
                .postId(testPostId2)
                .content("원본 댓글입니다.")
                .build();
        Integer commentId = commentService.createComment(commentDTO);

        CommentDTO updateDTO = CommentDTO.builder()
                .content("수정된 댓글입니다.")
                .build();

        // when
        commentService.updateComment(commentId, updateDTO);

        // then
        CommentDTO updatedComment = commentService.getCommentById(commentId);
        assertThat(updatedComment.getContent()).isEqualTo("수정된 댓글입니다.");
    }

    @Test
    @DisplayName("댓글 삭제 테스트")
    void deleteComment() {
        // given
        CommentDTO commentDTO = CommentDTO.builder()
                .userId(testUserId1)
                .postId(testPostId2)
                .content("삭제될 댓글입니다.")
                .build();
        Integer commentId = commentService.createComment(commentDTO);

        // when
        commentService.deleteComment(commentId);

        // then
        assertThat(commentService.getCommentCountByPostId(testPostId2)).isEqualTo(0);
        assertThatThrownBy(() -> commentService.getCommentById(commentId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 댓글 삭제 시 예외 발생 테스트")
    void deleteComment_NotFound() {
        // when & then
        assertThatThrownBy(() -> commentService.deleteComment(99999))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("ID로 댓글 조회 테스트")
    void getCommentById() {
        // given
        CommentDTO commentDTO = CommentDTO.builder()
                .userId(testUserId1)
                .postId(testPostId2)
                .content("조회할 댓글입니다.")
                .build();
        Integer commentId = commentService.createComment(commentDTO);

        // when
        CommentDTO result = commentService.getCommentById(commentId);

        // then
        assertThat(result.getId()).isEqualTo(commentId);
        assertThat(result.getContent()).isEqualTo("조회할 댓글입니다.");
        assertThat(result.getUserId()).isEqualTo(testUserId1);
        assertThat(result.getPostId()).isEqualTo(testPostId2);
    }

    @Test
    @DisplayName("게시글별 댓글 조회 테스트 (페이징, 최신순)")
    void getCommentsByPostIdWithPaging() {
        // given
        CommentDTO commentDTO1 = CommentDTO.builder()
                .userId(testUserId1)
                .postId(testPostId2)
                .content("첫 번째 댓글")
                .build();
        CommentDTO commentDTO2 = CommentDTO.builder()
                .userId(testUserId2)
                .postId(testPostId2)
                .content("두 번째 댓글")
                .build();
        commentService.createComment(commentDTO1);
        commentService.createComment(commentDTO2);

        Pageable pageable = PageRequest.of(0, 10);

        // when
        var result = commentService.getCommentsByPostIdWithPaging(testPostId2, pageable);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent().get(0).getContent()).isEqualTo("두 번째 댓글"); // 최신순
        assertThat(result.getContent().get(1).getContent()).isEqualTo("첫 번째 댓글");
    }

    @Test
    @DisplayName("게시글별 댓글 조회 테스트 (페이징, 오래된순)")
    void getCommentsByPostIdWithPagingAsc() {
        // given
        CommentDTO commentDTO1 = CommentDTO.builder()
                .userId(testUserId1)
                .postId(testPostId2)
                .content("첫 번째 댓글")
                .build();
        CommentDTO commentDTO2 = CommentDTO.builder()
                .userId(testUserId2)
                .postId(testPostId2)
                .content("두 번째 댓글")
                .build();
        commentService.createComment(commentDTO1);
        commentService.createComment(commentDTO2);

        Pageable pageable = PageRequest.of(0, 10);

        // when
        var result = commentService.getCommentsByPostIdWithPagingAsc(testPostId2, pageable);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent().get(0).getContent()).isEqualTo("첫 번째 댓글"); // 오래된순
        assertThat(result.getContent().get(1).getContent()).isEqualTo("두 번째 댓글");
    }

    @Test
    @DisplayName("사용자별 댓글 조회 테스트")
    void getCommentsByUserIdWithPaging() {
        // given
        CommentDTO commentDTO1 = CommentDTO.builder()
                .userId(testUserId1)
                .postId(testPostId1)
                .content("사용자1의 첫 번째 댓글")
                .build();
        CommentDTO commentDTO2 = CommentDTO.builder()
                .userId(testUserId1)
                .postId(testPostId2)
                .content("사용자1의 두 번째 댓글")
                .build();
        commentService.createComment(commentDTO1);
        commentService.createComment(commentDTO2);

        Pageable pageable = PageRequest.of(0, 10);

        // when
        var result = commentService.getCommentsByUserIdWithPaging(testUserId1, pageable);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).extracting("userId").containsOnly(testUserId1);
    }

    @Test
    @DisplayName("게시글별 댓글 수 조회 테스트")
    void getCommentCountByPostId() {
        // given
        CommentDTO commentDTO1 = CommentDTO.builder()
                .userId(testUserId1)
                .postId(testPostId2)
                .content("첫 번째 댓글")
                .build();
        CommentDTO commentDTO2 = CommentDTO.builder()
                .userId(testUserId2)
                .postId(testPostId2)
                .content("두 번째 댓글")
                .build();

        // when & then
        assertThat(commentService.getCommentCountByPostId(testPostId2)).isEqualTo(0);
        
        commentService.createComment(commentDTO1);
        assertThat(commentService.getCommentCountByPostId(testPostId2)).isEqualTo(1);
        
        commentService.createComment(commentDTO2);
        assertThat(commentService.getCommentCountByPostId(testPostId2)).isEqualTo(2);
    }

    @Test
    @DisplayName("사용자별 댓글 수 조회 테스트")
    void getCommentCountByUserId() {
        // given
        CommentDTO commentDTO1 = CommentDTO.builder()
                .userId(testUserId1)
                .postId(testPostId1)
                .content("첫 번째 댓글")
                .build();
        CommentDTO commentDTO2 = CommentDTO.builder()
                .userId(testUserId1)
                .postId(testPostId2)
                .content("두 번째 댓글")
                .build();

        // when & then
        assertThat(commentService.getCommentCountByUserId(testUserId1)).isEqualTo(0);
        
        commentService.createComment(commentDTO1);
        assertThat(commentService.getCommentCountByUserId(testUserId1)).isEqualTo(1);
        
        commentService.createComment(commentDTO2);
        assertThat(commentService.getCommentCountByUserId(testUserId1)).isEqualTo(2);
    }

    @Test
    @DisplayName("댓글 검색 테스트")
    void searchComments() {
        // given
        CommentDTO commentDTO1 = CommentDTO.builder()
                .userId(testUserId1)
                .postId(testPostId1)
                .content("테스트 댓글입니다.")
                .build();
        CommentDTO commentDTO2 = CommentDTO.builder()
                .userId(testUserId2)
                .postId(testPostId2)
                .content("다른 내용의 댓글입니다.")
                .build();
        commentService.createComment(commentDTO1);
        commentService.createComment(commentDTO2);

        // when
        List<CommentDTO> result = commentService.searchComments("테스트");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getContent()).contains("테스트");
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 댓글 생성 시 예외 발생 테스트")
    void createComment_UserNotFound() {
        // given
        CommentDTO commentDTO = CommentDTO.builder()
                .userId(99999) // 존재하지 않는 사용자 ID
                .postId(testPostId2)
                .content("테스트 댓글")
                .build();

        // when & then
        assertThatThrownBy(() -> commentService.createComment(commentDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("존재하지 않는 게시글로 댓글 생성 시 예외 발생 테스트")
    void createComment_PostNotFound() {
        // given
        CommentDTO commentDTO = CommentDTO.builder()
                .userId(testUserId1)
                .postId(99999) // 존재하지 않는 게시글 ID
                .content("테스트 댓글")
                .build();

        // when & then
        assertThatThrownBy(() -> commentService.createComment(commentDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("게시글을 찾을 수 없습니다");
    }

    @Test
    @DisplayName("DTO 변환 로직 테스트 - Entity to DTO")
    void entityToDTOTest() {
        // given
        CommentDTO commentDTO = CommentDTO.builder()
                .userId(testUserId1)
                .postId(testPostId2)
                .content("DTO 변환 테스트 댓글")
                .build();
        Integer commentId = commentService.createComment(commentDTO);

        // when
        CommentDTO result = commentService.getCommentById(commentId);

        // then
        assertThat(result.getUserId()).isEqualTo(testUserId1);
        assertThat(result.getPostId()).isEqualTo(testPostId2);
        assertThat(result.getContent()).isEqualTo("DTO 변환 테스트 댓글");
        assertThat(result.getUserName()).isNotNull();
        assertThat(result.getPostTitle()).isNotNull();
    }

    @Test
    @DisplayName("빈 결과에 대한 테스트")
    void emptyResultTest() {
        // when
        Pageable pageable = PageRequest.of(0, 10);
        var postResult = commentService.getCommentsByPostIdWithPaging(testPostId1, pageable);
        var userResult = commentService.getCommentsByUserIdWithPaging(testUserId1, pageable);
        List<CommentDTO> searchResult = commentService.searchComments("존재하지않는내용");

        // then
        assertThat(postResult.getContent()).isEmpty();
        assertThat(userResult.getContent()).isEmpty();
        assertThat(searchResult).isEmpty();
    }

    @Test
    @DisplayName("페이징 정상 동작 테스트")
    void pagingTest() {
        // given - 여러 댓글 생성
        for (int i = 1; i <= 15; i++) {
            CommentDTO commentDTO = CommentDTO.builder()
                    .userId(testUserId1)
                    .postId(testPostId1)
                    .content("댓글 " + i)
                    .build();
            commentService.createComment(commentDTO);
        }

        Pageable pageable = PageRequest.of(0, 10);

        // when
        var result = commentService.getCommentsByPostIdWithPaging(testPostId1, pageable);

        // then
        assertThat(result.getContent()).hasSize(10);
        assertThat(result.getTotalElements()).isEqualTo(15);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getNumber()).isEqualTo(0);
    }
} 