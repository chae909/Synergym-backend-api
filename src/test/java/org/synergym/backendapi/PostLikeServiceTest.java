package org.synergym.backendapi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.synergym.backendapi.dto.PostLikeDTO;
import org.synergym.backendapi.entity.Category;
import org.synergym.backendapi.entity.Post;
import org.synergym.backendapi.entity.User;
import org.synergym.backendapi.exception.EntityNotFoundException;
import org.synergym.backendapi.repository.CategoryRepository;
import org.synergym.backendapi.repository.PostRepository;
import org.synergym.backendapi.repository.UserRepository;
import org.synergym.backendapi.service.PostLikeService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PostLikeServiceTest {

    @Autowired
    private PostLikeService postLikeService;

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
        String uniqueEmail1 = "test_postlike_" + System.currentTimeMillis() + "_1@example.com";
        String uniqueEmail2 = "test_postlike_" + System.currentTimeMillis() + "_2@example.com";
        
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
    @DisplayName("좋아요 생성 테스트")
    void createPostLike() {
        // given
        PostLikeDTO postLikeDTO = PostLikeDTO.builder()
                .userId(testUserId1)
                .postId(testPostId2)
                .build();

        // when
        postLikeService.createPostLike(postLikeDTO);

        // then
        assertThat(postLikeService.existsByUserIdAndPostId(testUserId1, testPostId2)).isTrue();
        assertThat(postLikeService.getLikeCountByPostId(testPostId2)).isEqualTo(1);
    }

    @Test
    @DisplayName("중복 좋아요 생성 시 예외 발생 테스트")
    void createPostLike_Duplicate() {
        // given
        PostLikeDTO postLikeDTO = PostLikeDTO.builder()
                .userId(testUserId1)
                .postId(testPostId2)
                .build();

        // when & then
        postLikeService.createPostLike(postLikeDTO);
        assertThatThrownBy(() -> postLikeService.createPostLike(postLikeDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 좋아요를 누른 게시글입니다.");
    }

    @Test
    @DisplayName("좋아요 삭제 테스트")
    void deletePostLike() {
        // given
        PostLikeDTO postLikeDTO = PostLikeDTO.builder()
                .userId(testUserId1)
                .postId(testPostId2)
                .build();
        postLikeService.createPostLike(postLikeDTO);

        // when
        postLikeService.deletePostLike(testUserId1, testPostId2);

        // then
        assertThat(postLikeService.existsByUserIdAndPostId(testUserId1, testPostId2)).isFalse();
        assertThat(postLikeService.getLikeCountByPostId(testPostId2)).isEqualTo(0);
    }

    @Test
    @DisplayName("존재하지 않는 좋아요 삭제 시 예외 발생 테스트")
    void deletePostLike_NotFound() {
        // when & then
        assertThatThrownBy(() -> postLikeService.deletePostLike(testUserId1, testPostId2))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("사용자별 좋아요 조회 테스트")
    void getPostLikesByUserId() {
        // given
        PostLikeDTO postLikeDTO1 = PostLikeDTO.builder()
                .userId(testUserId1)
                .postId(testPostId2)
                .build();
        PostLikeDTO postLikeDTO2 = PostLikeDTO.builder()
                .userId(testUserId1)
                .postId(testPostId1)
                .build();
        postLikeService.createPostLike(postLikeDTO1);
        postLikeService.createPostLike(postLikeDTO2);

        // when
        List<PostLikeDTO> result = postLikeService.getPostLikesByUserId(testUserId1);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting("postId").contains(testPostId1, testPostId2);
        
        // DTO 변환 검증
        PostLikeDTO firstResult = result.stream()
                .filter(dto -> dto.getPostId().equals(testPostId1))
                .findFirst()
                .orElse(null);
        assertThat(firstResult).isNotNull();
        assertThat(firstResult.getUserId()).isEqualTo(testUserId1);
        assertThat(firstResult.getUserName()).isNotNull();
        assertThat(firstResult.getPostTitle()).isNotNull();
    }

    @Test
    @DisplayName("게시글별 좋아요 조회 테스트")
    void getPostLikesByPostId() {
        // given
        PostLikeDTO postLikeDTO1 = PostLikeDTO.builder()
                .userId(testUserId1)
                .postId(testPostId2)
                .build();
        PostLikeDTO postLikeDTO2 = PostLikeDTO.builder()
                .userId(testUserId2)
                .postId(testPostId2)
                .build();
        postLikeService.createPostLike(postLikeDTO1);
        postLikeService.createPostLike(postLikeDTO2);

        // when
        List<PostLikeDTO> result = postLikeService.getPostLikesByPostId(testPostId2);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting("userId").contains(testUserId1, testUserId2);
        
        // DTO 변환 검증
        PostLikeDTO firstResult = result.stream()
                .filter(dto -> dto.getUserId().equals(testUserId1))
                .findFirst()
                .orElse(null);
        assertThat(firstResult).isNotNull();
        assertThat(firstResult.getPostId()).isEqualTo(testPostId2);
        assertThat(firstResult.getUserName()).isNotNull();
        assertThat(firstResult.getPostTitle()).isNotNull();
    }

    @Test
    @DisplayName("좋아요 존재 여부 확인 테스트")
    void existsByUserIdAndPostId() {
        // given
        PostLikeDTO postLikeDTO = PostLikeDTO.builder()
                .userId(testUserId1)
                .postId(testPostId2)
                .build();

        // when & then
        assertThat(postLikeService.existsByUserIdAndPostId(testUserId1, testPostId2)).isFalse();
        
        postLikeService.createPostLike(postLikeDTO);
        assertThat(postLikeService.existsByUserIdAndPostId(testUserId1, testPostId2)).isTrue();
    }

    @Test
    @DisplayName("게시글별 좋아요 수 조회 테스트")
    void getLikeCountByPostId() {
        // given
        PostLikeDTO postLikeDTO1 = PostLikeDTO.builder()
                .userId(testUserId1)
                .postId(testPostId2)
                .build();
        PostLikeDTO postLikeDTO2 = PostLikeDTO.builder()
                .userId(testUserId2)
                .postId(testPostId2)
                .build();

        // when & then
        assertThat(postLikeService.getLikeCountByPostId(testPostId2)).isEqualTo(0);
        
        postLikeService.createPostLike(postLikeDTO1);
        assertThat(postLikeService.getLikeCountByPostId(testPostId2)).isEqualTo(1);
        
        postLikeService.createPostLike(postLikeDTO2);
        assertThat(postLikeService.getLikeCountByPostId(testPostId2)).isEqualTo(2);
    }

    @Test
    @DisplayName("사용자별 좋아요 수 조회 테스트")
    void getLikeCountByUserId() {
        // given
        PostLikeDTO postLikeDTO1 = PostLikeDTO.builder()
                .userId(testUserId1)
                .postId(testPostId2)
                .build();
        PostLikeDTO postLikeDTO2 = PostLikeDTO.builder()
                .userId(testUserId1)
                .postId(testPostId1)
                .build();

        // when & then
        assertThat(postLikeService.getLikeCountByUserId(testUserId1)).isEqualTo(0);
        
        postLikeService.createPostLike(postLikeDTO1);
        assertThat(postLikeService.getLikeCountByUserId(testUserId1)).isEqualTo(1);
        
        postLikeService.createPostLike(postLikeDTO2);
        assertThat(postLikeService.getLikeCountByUserId(testUserId1)).isEqualTo(2);
    }

    @Test
    @DisplayName("좋아요 생성 후 삭제 시 likeCount 정상 동작 테스트")
    void likeCountAfterCreateAndDelete() {
        // given
        PostLikeDTO postLikeDTO = PostLikeDTO.builder()
                .userId(testUserId1)
                .postId(testPostId2)
                .build();

        // when & then
        assertThat(postLikeService.getLikeCountByPostId(testPostId2)).isEqualTo(0);
        
        postLikeService.createPostLike(postLikeDTO);
        assertThat(postLikeService.getLikeCountByPostId(testPostId2)).isEqualTo(1);
        
        postLikeService.deletePostLike(testUserId1, testPostId2);
        assertThat(postLikeService.getLikeCountByPostId(testPostId2)).isEqualTo(0);
    }

    @Test
    @DisplayName("DTO 변환 로직 테스트 - Entity to DTO")
    void entityToDTOTest() {
        // given
        PostLikeDTO postLikeDTO = PostLikeDTO.builder()
                .userId(testUserId1)
                .postId(testPostId2)
                .build();
        postLikeService.createPostLike(postLikeDTO);

        // when
        List<PostLikeDTO> result = postLikeService.getPostLikesByUserId(testUserId1);
        PostLikeDTO resultDTO = result.get(0);

        // then
        assertThat(resultDTO.getUserId()).isEqualTo(testUserId1);
        assertThat(resultDTO.getUserName()).isNotNull();
        assertThat(resultDTO.getPostId()).isEqualTo(testPostId2);
        assertThat(resultDTO.getPostTitle()).isNotNull();
    }

    @Test
    @DisplayName("DTO 변환 로직 테스트 - DTO to Entity")
    void dtoToEntityTest() {
        // given
        PostLikeDTO postLikeDTO = PostLikeDTO.builder()
                .userId(testUserId1)
                .postId(testPostId2)
                .build();

        // when
        postLikeService.createPostLike(postLikeDTO);

        // then - Service를 통한 검증
        assertThat(postLikeService.existsByUserIdAndPostId(testUserId1, testPostId2)).isTrue();
        
        List<PostLikeDTO> result = postLikeService.getPostLikesByUserId(testUserId1);
        assertThat(result).hasSize(1);
        PostLikeDTO resultDTO = result.get(0);
        assertThat(resultDTO.getUserId()).isEqualTo(testUserId1);
        assertThat(resultDTO.getPostId()).isEqualTo(testPostId2);
    }

    @Test
    @DisplayName("빈 결과에 대한 DTO 변환 테스트")
    void emptyResultDTOTest() {
        // when
        List<PostLikeDTO> userResult = postLikeService.getPostLikesByUserId(testUserId1);
        List<PostLikeDTO> postResult = postLikeService.getPostLikesByPostId(testPostId1);

        // then
        assertThat(userResult).isEmpty();
        assertThat(postResult).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 좋아요 생성 시 예외 발생 테스트")
    void createPostLike_UserNotFound() {
        // given
        PostLikeDTO postLikeDTO = PostLikeDTO.builder()
                .userId(99999) // 존재하지 않는 사용자 ID
                .postId(testPostId2)
                .build();

        // when & then
        assertThatThrownBy(() -> postLikeService.createPostLike(postLikeDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("존재하지 않는 게시글로 좋아요 생성 시 예외 발생 테스트")
    void createPostLike_PostNotFound() {
        // given
        PostLikeDTO postLikeDTO = PostLikeDTO.builder()
                .userId(testUserId1)
                .postId(99999) // 존재하지 않는 게시글 ID
                .build();

        // when & then
        assertThatThrownBy(() -> postLikeService.createPostLike(postLikeDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("게시글을 찾을 수 없습니다");
    }

    @Test
    @DisplayName("여러 사용자가 같은 게시글에 좋아요 누르기 테스트")
    void multipleUsersLikeSamePost() {
        // given
        PostLikeDTO postLikeDTO1 = PostLikeDTO.builder()
                .userId(testUserId1)
                .postId(testPostId2)
                .build();
        PostLikeDTO postLikeDTO2 = PostLikeDTO.builder()
                .userId(testUserId2)
                .postId(testPostId2)
                .build();

        // when
        postLikeService.createPostLike(postLikeDTO1);
        postLikeService.createPostLike(postLikeDTO2);

        // then
        assertThat(postLikeService.getLikeCountByPostId(testPostId2)).isEqualTo(2);
        assertThat(postLikeService.getPostLikesByPostId(testPostId2)).hasSize(2);
    }

    @Test
    @DisplayName("한 사용자가 여러 게시글에 좋아요 누르기 테스트")
    void oneUserLikesMultiplePosts() {
        // given
        PostLikeDTO postLikeDTO1 = PostLikeDTO.builder()
                .userId(testUserId1)
                .postId(testPostId1)
                .build();
        PostLikeDTO postLikeDTO2 = PostLikeDTO.builder()
                .userId(testUserId1)
                .postId(testPostId2)
                .build();

        // when
        postLikeService.createPostLike(postLikeDTO1);
        postLikeService.createPostLike(postLikeDTO2);

        // then
        assertThat(postLikeService.getLikeCountByUserId(testUserId1)).isEqualTo(2);
        assertThat(postLikeService.getPostLikesByUserId(testUserId1)).hasSize(2);
    }

    @Test
    @DisplayName("좋아요 삭제 후 재생성 테스트")
    void deleteAndRecreateLike() {
        // given
        PostLikeDTO postLikeDTO = PostLikeDTO.builder()
                .userId(testUserId1)
                .postId(testPostId2)
                .build();

        // when
        postLikeService.createPostLike(postLikeDTO);
        assertThat(postLikeService.getLikeCountByPostId(testPostId2)).isEqualTo(1);
        
        postLikeService.deletePostLike(testUserId1, testPostId2);
        assertThat(postLikeService.getLikeCountByPostId(testPostId2)).isEqualTo(0);
        
        // 재생성
        postLikeService.createPostLike(postLikeDTO);

        // then
        assertThat(postLikeService.getLikeCountByPostId(testPostId2)).isEqualTo(1);
        assertThat(postLikeService.existsByUserIdAndPostId(testUserId1, testPostId2)).isTrue();
    }

    @Test
    @DisplayName("사용자별 좋아요 조회 시 DTO 필드 매핑 검증")
    void getPostLikesByUserId_DTOFieldMapping() {
        // given
        PostLikeDTO postLikeDTO = PostLikeDTO.builder()
                .userId(testUserId1)
                .postId(testPostId2)
                .build();
        postLikeService.createPostLike(postLikeDTO);

        // when
        List<PostLikeDTO> result = postLikeService.getPostLikesByUserId(testUserId1);
        PostLikeDTO resultDTO = result.get(0);

        // then
        assertThat(resultDTO.getUserId()).isEqualTo(testUserId1);
        assertThat(resultDTO.getUserName()).isNotNull();
        assertThat(resultDTO.getPostId()).isEqualTo(testPostId2);
        assertThat(resultDTO.getPostTitle()).isNotNull();
    }

    @Test
    @DisplayName("게시글별 좋아요 조회 시 DTO 필드 매핑 검증")
    void getPostLikesByPostId_DTOFieldMapping() {
        // given
        PostLikeDTO postLikeDTO = PostLikeDTO.builder()
                .userId(testUserId1)
                .postId(testPostId2)
                .build();
        postLikeService.createPostLike(postLikeDTO);

        // when
        List<PostLikeDTO> result = postLikeService.getPostLikesByPostId(testPostId2);
        PostLikeDTO resultDTO = result.get(0);

        // then
        assertThat(resultDTO.getUserId()).isEqualTo(testUserId1);
        assertThat(resultDTO.getUserName()).isNotNull();
        assertThat(resultDTO.getPostId()).isEqualTo(testPostId2);
        assertThat(resultDTO.getPostTitle()).isNotNull();
    }
} 