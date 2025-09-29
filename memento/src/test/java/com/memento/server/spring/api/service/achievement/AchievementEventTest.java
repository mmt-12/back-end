package com.memento.server.spring.api.service.achievement;

import static com.memento.server.config.MinioProperties.FileType.EMOJI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.memento.server.api.controller.member.dto.MemberSignUpResponse;
import com.memento.server.api.controller.memory.dto.CreateUpdateMemoryRequest;
import com.memento.server.api.service.achievement.AchievementService;
import com.memento.server.api.service.auth.jwt.JwtToken;
import com.memento.server.api.service.auth.jwt.JwtTokenProvider;
import com.memento.server.api.service.auth.jwt.MemberClaim;
import com.memento.server.api.service.comment.CommentService;
import com.memento.server.api.service.comment.dto.request.EmojiCommentCreateServiceRequest;
import com.memento.server.api.service.emoji.EmojiService;
import com.memento.server.api.service.emoji.dto.request.EmojiCreateServiceRequest;
import com.memento.server.api.service.guestBook.GuestBookService;
import com.memento.server.api.service.mbti.MbtiService;
import com.memento.server.api.service.member.MemberService;
import com.memento.server.api.service.memory.MemoryService;
import com.memento.server.api.service.post.PostService;
import com.memento.server.api.service.profileImage.ProfileImageService;
import com.memento.server.associate.AssociateFixtures;
import com.memento.server.comment.CommentFixtures;
import com.memento.server.common.fixture.CommonFixtures;
import com.memento.server.community.CommunityFixtures;
import com.memento.server.config.MinioProperties;
import com.memento.server.domain.achievement.Achievement;
import com.memento.server.domain.achievement.AchievementAssociateRepository;
import com.memento.server.domain.achievement.AchievementRepository;
import com.memento.server.domain.achievement.AchievementType;
import com.memento.server.domain.comment.Comment;
import com.memento.server.domain.comment.CommentRepository;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.community.AssociateStats;
import com.memento.server.domain.community.AssociateStatsRepository;
import com.memento.server.domain.community.Community;
import com.memento.server.domain.community.CommunityRepository;
import com.memento.server.domain.emoji.Emoji;
import com.memento.server.domain.emoji.EmojiRepository;
import com.memento.server.domain.event.Event;
import com.memento.server.domain.event.EventRepository;
import com.memento.server.domain.guestBook.GuestBookRepository;
import com.memento.server.domain.guestBook.GuestBookType;
import com.memento.server.domain.mbti.Mbti;
import com.memento.server.domain.mbti.MbtiTest;
import com.memento.server.domain.mbti.MbtiTestRepository;
import com.memento.server.domain.member.Member;
import com.memento.server.domain.member.MemberRepository;
import com.memento.server.domain.memory.Memory;
import com.memento.server.domain.memory.MemoryRepository;
import com.memento.server.domain.post.Hash;
import com.memento.server.domain.post.Post;
import com.memento.server.domain.post.PostImage;
import com.memento.server.domain.post.PostImageRepository;
import com.memento.server.domain.post.PostRepository;
import com.memento.server.domain.profileImage.ProfileImage;
import com.memento.server.domain.profileImage.ProfileImageRepository;
import com.memento.server.emoji.EmojiFixtures;
import com.memento.server.event.EventFixtures;
import com.memento.server.member.MemberFixtures;
import com.memento.server.memory.MemoryFixtures;
import com.memento.server.post.PostFixtures;
import com.memento.server.spring.api.service.IntegrationsTestSupport;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AchievementEventTest extends IntegrationsTestSupport {

	@Autowired
	private ProfileImageService profileImageService;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private CommunityRepository communityRepository;

	@Autowired
	private AssociateRepository associateRepository;

	@Autowired
	private AssociateStatsRepository associateStatsRepository;

	@Autowired
	private AchievementAssociateRepository achievementAssociateRepository;

	@Autowired
	private ProfileImageRepository profileImageRepository;

	@Autowired
	private AchievementRepository achievementRepository;

	@Autowired
	private MbtiService mbtiService;

	@Autowired
	private MbtiTestRepository mbtiTestRepository;

	@Autowired
	private GuestBookService guestBookService;

	@Autowired
	private MemoryService memoryService;

	@Autowired
	private EventRepository eventRepository;

	@Autowired
	private MemoryRepository memoryRepository;

	@Autowired
	private PostService postService;

	@Autowired
	private PostImageRepository postImageRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private EmojiService emojiService;

	@Autowired
	private EmojiRepository emojiRepository;

	@Autowired
	private CommentService commentService;

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private GuestBookRepository guestBookRepository;

	@Autowired
	private MemberService memberService;

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Autowired
	private AchievementService achievementService;

	@AfterEach
	void afterEach() {
		commentRepository.deleteAll();
		emojiRepository.deleteAll();
		guestBookRepository.deleteAll();
		postImageRepository.deleteAll();
		postRepository.deleteAll();
		memoryRepository.deleteAll();
		eventRepository.deleteAll();
		mbtiTestRepository.deleteAll();
		profileImageRepository.deleteAll();
		associateStatsRepository.deleteAll();
		achievementAssociateRepository.deleteAll();
		associateRepository.deleteAll();
		communityRepository.deleteAll();
		memberRepository.deleteAll();
	}

	@BeforeAll
	void setupAchievements() {
		for(int i = 0; i < 35; i++){
			achievementRepository.save(Achievement.builder()
				.type(AchievementType.OPEN)
				.criteria("test")
				.name("test")
				.build());
		}
	}

	@Test
	@DisplayName("변검술사")
	void profileImageRegisteredTest() throws Exception {
		// given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate associate = AssociateFixtures.associate(member, community);
		associateRepository.save(associate);

		Associate registrant = AssociateFixtures.associate(member, community);
		associateRepository.save(registrant);

		associateStatsRepository.save(AssociateStats.builder()
				.associate(associate)
			.build());

		for (int i = 0; i < 20; i++) {
			profileImageRepository.save(ProfileImage.builder()
				.url("test.png")
				.associate(associate)
				.registrant(registrant)
				.build());
		}

		MultipartFile file = new MockMultipartFile("image", "test.png", "image/png", "test".getBytes());
		String url = "https://example.com/test.png";
		given(minioService.createFile(file, MinioProperties.FileType.PROFILE_IMAGE))
			.willReturn(url);

		// when
		profileImageService.create(community.getId(), associate.getId(),registrant.getId(), file);

		// then
		Thread.sleep(1000);
		AssociateStats associateStats = associateStatsRepository.findByAssociateId(associate.getId()).orElseThrow();
		assertThat(associateStats.getRegisteredProfileImageCount()).isEqualTo(21);
		assertThat(achievementAssociateRepository.existsByAchievementIdAndAssociateId(8L, associate.getId()))
			.isTrue();
	}

	@Test
	@DisplayName("파파라치")
	void profileImageUploadedTest() throws Exception {
		// given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate associate = AssociateFixtures.associate(member, community);
		associateRepository.save(associate);

		Associate registrant = AssociateFixtures.associate(member, community);
		associateRepository.save(registrant);

		associateStatsRepository.save(AssociateStats.builder()
			.associate(registrant)
			.build());

		for(int i = 0; i < 9; i++){
			achievementRepository.save(Achievement.builder()
				.type(AchievementType.OPEN)
				.criteria("test")
				.name("test")
				.build());
		}

		for (int i = 0; i < 29; i++) {
			profileImageRepository.save(ProfileImage.builder()
				.url("test.png")
				.associate(associate)
				.registrant(registrant)
				.build());
		}

		MultipartFile file = new MockMultipartFile("image", "test.png", "image/png", "test".getBytes());
		String url = "https://example.com/test.png";
		given(minioService.createFile(file, MinioProperties.FileType.PROFILE_IMAGE))
			.willReturn(url);

		// when
		profileImageService.create(community.getId(), associate.getId(),registrant.getId(), file);

		// then
		Thread.sleep(1000);
		AssociateStats associateStats = associateStatsRepository.findByAssociateId(registrant.getId()).orElseThrow();
		assertThat(associateStats.getUploadedProfileImageCount()).isEqualTo(30);
		assertThat(achievementAssociateRepository.existsByAchievementIdAndAssociateId(9L, registrant.getId()))
			.isTrue();
	}

	@Test
	@DisplayName("관상가")
	void mbtiCreateTest() throws Exception {
		// given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate associate1 = AssociateFixtures.associate(member, community);
		associateRepository.save(associate1);

		Associate associate2 = AssociateFixtures.associate(member, community);
		associateRepository.save(associate2);

		associateStatsRepository.save(AssociateStats.builder()
			.associate(associate1)
			.build());

		associateStatsRepository.save(AssociateStats.builder()
			.associate(associate2)
			.build());

		// when
		mbtiService.create(community.getId(), associate1.getId(), associate2.getId(), Mbti.ENFP);

		// then
		Thread.sleep(1000);
		assertThat(achievementAssociateRepository.existsByAchievementIdAndAssociateId(2L, associate1.getId()))
			.isTrue();
	}

	@Test
	@DisplayName("다중인격")
	void mbtiTotalCreateTest() throws Exception {
		// given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate associate1 = AssociateFixtures.associate(member, community);
		associateRepository.save(associate1);

		Associate associate2 = AssociateFixtures.associate(member, community);
		associateRepository.save(associate2);

		Associate associate3 = AssociateFixtures.associate(member, community);
		associateRepository.save(associate3);

		associateStatsRepository.save(AssociateStats.builder()
			.associate(associate1)
			.build());

		associateStatsRepository.save(AssociateStats.builder()
			.associate(associate3)
			.build());

		MbtiTest mbtiTest1 = MbtiTest.builder()
			.fromAssociate(associate2)
			.toAssociate(associate1)
			.mbti(Mbti.ENFP)
			.build();
		MbtiTest mbtiTest2 = MbtiTest.builder()
			.fromAssociate(associate2)
			.toAssociate(associate1)
			.mbti(Mbti.ENFJ)
			.build();
		MbtiTest mbtiTest3 = MbtiTest.builder()
			.fromAssociate(associate2)
			.toAssociate(associate1)
			.mbti(Mbti.ENTJ)
			.build();
		MbtiTest mbtiTest4 = MbtiTest.builder()
			.fromAssociate(associate2)
			.toAssociate(associate1)
			.mbti(Mbti.INFP)
			.build();
		MbtiTest mbtiTest5 = MbtiTest.builder()
			.fromAssociate(associate2)
			.toAssociate(associate1)
			.mbti(Mbti.INFJ)
			.build();
		mbtiTestRepository.saveAll(List.of(mbtiTest1, mbtiTest2, mbtiTest3, mbtiTest4, mbtiTest5));

		// when
		mbtiService.create(community.getId(), associate3.getId(), associate1.getId(), Mbti.ESFP);

		// then
		Thread.sleep(1000);
		assertThat(achievementAssociateRepository.existsByAchievementIdAndAssociateId(3L, associate1.getId()))
			.isTrue();
	}

	@Test
	@DisplayName("FFFFFF")
	void fMbtiCreateTest() throws Exception {
		// given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate associate1 = AssociateFixtures.associate(member, community);
		associateRepository.save(associate1);

		Associate associate2 = AssociateFixtures.associate(member, community);
		associateRepository.save(associate2);

		Associate associate3 = AssociateFixtures.associate(member, community);
		associateRepository.save(associate3);

		associateStatsRepository.save(AssociateStats.builder()
			.associate(associate1)
			.build());

		associateStatsRepository.save(AssociateStats.builder()
			.associate(associate3)
			.build());

		MbtiTest mbtiTest1 = MbtiTest.builder()
			.fromAssociate(associate2)
			.toAssociate(associate1)
			.mbti(Mbti.ISFP)
			.build();
		MbtiTest mbtiTest2 = MbtiTest.builder()
			.fromAssociate(associate2)
			.toAssociate(associate1)
			.mbti(Mbti.ISFJ)
			.build();
		MbtiTest mbtiTest3 = MbtiTest.builder()
			.fromAssociate(associate2)
			.toAssociate(associate1)
			.mbti(Mbti.INFP)
			.build();
		MbtiTest mbtiTest4 = MbtiTest.builder()
			.fromAssociate(associate2)
			.toAssociate(associate1)
			.mbti(Mbti.INFJ)
			.build();
		MbtiTest mbtiTest5 = MbtiTest.builder()
			.fromAssociate(associate2)
			.toAssociate(associate1)
			.mbti(Mbti.ESFP)
			.build();
		MbtiTest mbtiTest6 = MbtiTest.builder()
			.fromAssociate(associate2)
			.toAssociate(associate1)
			.mbti(Mbti.ESFJ)
			.build();
		MbtiTest mbtiTest7 = MbtiTest.builder()
			.fromAssociate(associate2)
			.toAssociate(associate1)
			.mbti(Mbti.ENFP)
			.build();
		mbtiTestRepository.saveAll(List.of(mbtiTest1, mbtiTest2, mbtiTest3, mbtiTest4, mbtiTest5, mbtiTest6, mbtiTest7));

		// when
		mbtiService.create(community.getId(), associate3.getId(), associate1.getId(), Mbti.ENFJ);

		// then
		Thread.sleep(1000);
		assertThat(achievementAssociateRepository.existsByAchievementIdAndAssociateId(4L, associate1.getId()))
			.isTrue();
	}

	@Test
	@DisplayName("T발C야?")
	void tMbtiCreateTest() throws Exception {
		// given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate associate1 = AssociateFixtures.associate(member, community);
		associateRepository.save(associate1);

		Associate associate2 = AssociateFixtures.associate(member, community);
		associateRepository.save(associate2);

		Associate associate3 = AssociateFixtures.associate(member, community);
		associateRepository.save(associate3);

		associateStatsRepository.save(AssociateStats.builder()
			.associate(associate1)
			.build());

		associateStatsRepository.save(AssociateStats.builder()
			.associate(associate3)
			.build());

		MbtiTest mbtiTest1 = MbtiTest.builder()
			.fromAssociate(associate2)
			.toAssociate(associate1)
			.mbti(Mbti.ISTP)
			.build();
		MbtiTest mbtiTest2 = MbtiTest.builder()
			.fromAssociate(associate2)
			.toAssociate(associate1)
			.mbti(Mbti.ISTJ)
			.build();
		MbtiTest mbtiTest3 = MbtiTest.builder()
			.fromAssociate(associate2)
			.toAssociate(associate1)
			.mbti(Mbti.INTP)
			.build();
		MbtiTest mbtiTest4 = MbtiTest.builder()
			.fromAssociate(associate2)
			.toAssociate(associate1)
			.mbti(Mbti.INTJ)
			.build();
		MbtiTest mbtiTest5 = MbtiTest.builder()
			.fromAssociate(associate2)
			.toAssociate(associate1)
			.mbti(Mbti.ESTP)
			.build();
		MbtiTest mbtiTest6 = MbtiTest.builder()
			.fromAssociate(associate2)
			.toAssociate(associate1)
			.mbti(Mbti.ESTJ)
			.build();
		MbtiTest mbtiTest7 = MbtiTest.builder()
			.fromAssociate(associate2)
			.toAssociate(associate1)
			.mbti(Mbti.ENTP)
			.build();
		mbtiTestRepository.saveAll(List.of(mbtiTest1, mbtiTest2, mbtiTest3, mbtiTest4, mbtiTest5, mbtiTest6, mbtiTest7));

		// when
		mbtiService.create(community.getId(), associate3.getId(), associate1.getId(), Mbti.ENTJ);

		// then
		Thread.sleep(1000);
		assertThat(achievementAssociateRepository.existsByAchievementIdAndAssociateId(5L, associate1.getId()))
			.isTrue();
	}

	@Test
	@DisplayName("마니또")
	void guestBookCountTest() throws Exception {
		// given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate register = AssociateFixtures.associate(member, community);
		associateRepository.save(register);

		Associate associate = AssociateFixtures.associate(member, community);
		associateRepository.save(associate);

		associateStatsRepository.save(AssociateStats.builder()
			.associate(register)
			.guestBookCount(49)
			.build());

		String content = "test";

		// when
		guestBookService.create(community.getId(), register.getId(), associate.getId(), GuestBookType.TEXT, null, content);

		// then
		Thread.sleep(1000);
		assertThat(achievementAssociateRepository.existsByAchievementIdAndAssociateId(11L, register.getId()))
			.isTrue();
	}

	@Test
	@DisplayName("팅팅팅")
	void guestBookWordTest() throws Exception {
		// given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate register = AssociateFixtures.associate(member, community);
		associateRepository.save(register);

		Associate associate = AssociateFixtures.associate(member, community);
		associateRepository.save(associate);

		associateStatsRepository.save(AssociateStats.builder()
			.associate(register)
			.build());

		String content = "팅팅팅";

		// when
		guestBookService.create(community.getId(), register.getId(), associate.getId(), GuestBookType.TEXT, null, content);

		// then
		Thread.sleep(1000);
		assertThat(achievementAssociateRepository.existsByAchievementIdAndAssociateId(18L, register.getId()))
			.isTrue();
	}

	@Test
	@DisplayName("민들레?노브랜드?")
	void memoryCreateTest() {
		// given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate creator = AssociateFixtures.associate(member, community);
		associateRepository.save(creator);

		Associate associate = AssociateFixtures.associate(member, community);
		associateRepository.save(associate);

		associateStatsRepository.save(AssociateStats.builder()
			.associate(creator)
			.build());

		CreateUpdateMemoryRequest.LocationRequest locationRequest = CreateUpdateMemoryRequest.LocationRequest.builder()
			.address("테스트 주소")
			.name("테스트 장소")
			.latitude(10.0F)
			.longitude(20.0F)
			.code(1)
			.build();

		CreateUpdateMemoryRequest.PeriodRequest periodRequest = CreateUpdateMemoryRequest.PeriodRequest.builder()
			.startTime(LocalDateTime.of(2024, 8, 1, 10, 0))
			.endTime(LocalDateTime.of(2024, 8, 1, 11, 0))
			.build();

		CreateUpdateMemoryRequest request = CreateUpdateMemoryRequest.builder()
			.title("새로운 추억")
			.description("새로운 추억에 대한 설명입니다.")
			.location(locationRequest)
			.period(periodRequest)
			.associates(List.of(creator.getId(), associate.getId()))
			.build();

		// when
		for(int i = 0; i < 10; i++) {
			memoryService.create(community.getId(), creator.getId(), request);
		}
		// then
		assertThat(achievementAssociateRepository.existsByAchievementIdAndAssociateId(12L, creator.getId()))
			.isTrue();
	}

	// 날짜 조정 불가
	// @Test
	// @DisplayName("13일의 금요일")
	// void MemoryDateTest() {
	// 	// given
	// 	Member member = MemberFixtures.member();
	// 	memberRepository.save(member);
	//
	// 	Community community = CommunityFixtures.community(member);
	// 	communityRepository.save(community);
	//
	// 	Associate creator = AssociateFixtures.associate(member, community);
	// 	associateRepository.save(creator);
	//
	// 	Associate associate = AssociateFixtures.associate(member, community);
	// 	associateRepository.save(associate);
	//
	// 	associateStatsRepository.save(AssociateStats.builder()
	// 		.associate(creator)
	// 		.build());
	//
	// 	CreateUpdateMemoryRequest.LocationRequest locationRequest = CreateUpdateMemoryRequest.LocationRequest.builder()
	// 		.address("테스트 주소")
	// 		.name("테스트 장소")
	// 		.latitude(10.0F)
	// 		.longitude(20.0F)
	// 		.code(1)
	// 		.build();
	//
	// 	CreateUpdateMemoryRequest.PeriodRequest periodRequest = CreateUpdateMemoryRequest.PeriodRequest.builder()
	// 		.startTime(LocalDateTime.of(2024, 8, 1, 10, 0))
	// 		.endTime(LocalDateTime.of(2024, 8, 1, 11, 0))
	// 		.build();
	//
	// 	CreateUpdateMemoryRequest request = CreateUpdateMemoryRequest.builder()
	// 		.title("새로운 추억")
	// 		.description("새로운 추억에 대한 설명입니다.")
	// 		.location(locationRequest)
	// 		.period(periodRequest)
	// 		.associates(List.of(creator.getId(), associate.getId()))
	// 		.build();
	//
	// 	// when
	// 	memoryService.create(community.getId(), creator.getId(), request);
	//
	// 	// then
	// 	assertThat(achievementAssociateRepository.existsByAchievementIdAndAssociateId(16L, creator.getId()))
	// 		.isTrue();
	// }

	@Test
	@DisplayName("GMG")
	void memoryJoinedTest() {
		// given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate creator = AssociateFixtures.associate(member, community);
		associateRepository.save(creator);

		Associate associate = AssociateFixtures.associate(member, community);
		associateRepository.save(associate);

		associateStatsRepository.save(AssociateStats.builder()
			.associate(creator)
			.build());

		associateStatsRepository.save(AssociateStats.builder()
			.associate(associate)
			.build());

		CreateUpdateMemoryRequest.LocationRequest locationRequest = CreateUpdateMemoryRequest.LocationRequest.builder()
			.address("테스트 주소")
			.name("테스트 장소")
			.latitude(10.0F)
			.longitude(20.0F)
			.code(1)
			.build();

		CreateUpdateMemoryRequest.PeriodRequest periodRequest = CreateUpdateMemoryRequest.PeriodRequest.builder()
			.startTime(LocalDateTime.of(2024, 8, 1, 10, 0))
			.endTime(LocalDateTime.of(2024, 8, 1, 11, 0))
			.build();

		CreateUpdateMemoryRequest request = CreateUpdateMemoryRequest.builder()
			.title("새로운 추억")
			.description("새로운 추억에 대한 설명입니다.")
			.location(locationRequest)
			.period(periodRequest)
			.associates(List.of(creator.getId(), associate.getId()))
			.build();

		// when
		for(int i = 0; i < 12; i++) {
			memoryService.create(community.getId(), creator.getId(), request);
		}

		// then
		assertThat(achievementAssociateRepository.existsByAchievementIdAndAssociateId(13L, associate.getId()))
			.isTrue();
	}

	@Test
	@DisplayName("전문찍새")
	public void postImageTest() throws IOException {
		//given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate associate = AssociateFixtures.associate(member, community);
		associateRepository.save(associate);

		associateStatsRepository.save(AssociateStats.builder()
			.associate(associate)
			.build());

		Event event = EventFixtures.event(community, associate);
		eventRepository.save(event);

		Memory memory = MemoryFixtures.memory(event);
		memoryRepository.save(memory);

		Post post = PostFixtures.post(memory, associate);
		postRepository.save(post);

		for(int i = 0; i < 99; i++) {
			postImageRepository.save(PostImage.builder()
				.hash(Hash.builder()
					.hash("test" + i)
					.build())
				.url("test")
				.post(post)
				.build());
		}

		BufferedImage bufferedImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, "png", baos);
		byte[] imageBytes = baos.toByteArray();

		MultipartFile file = new MockMultipartFile("image", "test.png", "image/png", imageBytes);
		String url = "https://example.com/test.png";
		given(minioService.createFile(file, MinioProperties.FileType.POST))
			.willReturn(url);

		String content = "test";

		//when
		postService.create(community.getId(), memory.getId(), associate.getId(), content, List.of(file));

		//then
		assertThat(achievementAssociateRepository.existsByAchievementIdAndAssociateId(10L, associate.getId()))
			.isTrue();
	}

	@Test
	@DisplayName("리액션공장")
	void reactionCreateTest() {
		// given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate associate = AssociateFixtures.associate(member, community);
		associateRepository.save(associate);

		associateStatsRepository.save(AssociateStats.builder()
			.associate(associate)
			.build());
		MultipartFile file = CommonFixtures.emojiFile();
		String url = "test";
		String name = "test";

		given(minioService.createFile(file, EMOJI))
			.willReturn(url);

		for(int i = 0; i < 19; i++) {
			Emoji emoji = EmojiFixtures.emoji(associate);
			emojiRepository.save(emoji);
		}

		// when
		EmojiCreateServiceRequest request = EmojiCreateServiceRequest.builder()
			.name(name)
			.associateId(associate.getId())
			.emoji(file)
			.build();

		emojiService.createEmoji(request);

		// then
		assertThat(achievementAssociateRepository.existsByAchievementIdAndAssociateId(6L, associate.getId()))
			.isTrue();
	}

	@Test
	@DisplayName("씽씽씽")
	void reactionNameTest() {
		// given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate associate = AssociateFixtures.associate(member, community);
		associateRepository.save(associate);

		associateStatsRepository.save(AssociateStats.builder()
			.associate(associate)
			.build());

		MultipartFile file = CommonFixtures.emojiFile();
		String url = "test";
		String name = "씽씽씽";

		given(minioService.createFile(file, EMOJI))
			.willReturn(url);

		// when
		EmojiCreateServiceRequest request = EmojiCreateServiceRequest.builder()
			.name(name)
			.associateId(associate.getId())
			.emoji(file)
			.build();

		emojiService.createEmoji(request);

		// then
		assertThat(achievementAssociateRepository.existsByAchievementIdAndAssociateId(17L, associate.getId()))
			.isTrue();
	}

	@Test
	@DisplayName("이모지 댓글을 생성한다.")
	void commentCreateTest() {
		// given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate associate = AssociateFixtures.associate(member, community);
		associateRepository.save(associate);

		associateStatsRepository.save(AssociateStats.builder()
			.associate(associate)
			.build());

		Event event = EventFixtures.event(community, associate);
		eventRepository.save(event);

		Memory memory = MemoryFixtures.memory(event);
		memoryRepository.save(memory);

		Post post = PostFixtures.post(memory, associate);
		postRepository.save(post);

		Emoji emoji = EmojiFixtures.emoji(associate);
		emojiRepository.save(emoji);

		for(int i = 0; i < 499; i++) {
			Comment comment = CommentFixtures.comment(post, associate);
			commentRepository.save(comment);
		}

		EmojiCommentCreateServiceRequest request = EmojiCommentCreateServiceRequest.of(
			emoji.getId(), post.getId(), associate.getId());

		// when
		commentService.createEmojiComment(request);

		// then
		assertThat(achievementAssociateRepository.existsByAchievementIdAndAssociateId(7L, associate.getId()))
			.isTrue();
	}

	@Test
	@DisplayName("방명록 전용 업적")
	void guestBookExclusiveTest() {
		// given
		Member member1 = Member.builder()
			.name("준수")
			.email("test")
			.birthday(LocalDate.of(1999,10,13))
			.kakaoId(1L)
			.build();
		memberRepository.save(member1);

		Community community = CommunityFixtures.community(member1, "SSAFY 12기 12반");
		communityRepository.save(community);

		Associate register = AssociateFixtures.associate(member1, community);
		associateRepository.save(register);

		Member member2 = Member.builder()
			.name("경완")
			.email("test")
			.birthday(LocalDate.of(1997,5,20))
			.kakaoId(1L)
			.build();
		memberRepository.save(member2);

		Associate associate = AssociateFixtures.associate(member2, community);
		associateRepository.save(associate);

		associateStatsRepository.save(AssociateStats.builder()
			.associate(register)
			.build());

		String content = "test";

		// when
		guestBookService.create(community.getId(), register.getId(), associate.getId(), GuestBookType.TEXT, null, content);

		// then
		assertThat(achievementAssociateRepository.existsByAchievementIdAndAssociateId(27L, register.getId()))
			.isTrue();
	}

	@Test
	@DisplayName("가입 전용 업적")
	void associateExclusiveTest() {
		// given
		Long kakaoId = 1L;
		String name = "오준수";
		String email = "test@test.com";
		LocalDate birthday = LocalDate.of(1999, 10, 13);

		// when
		MemberSignUpResponse response = memberService.signUp(kakaoId, name, email, birthday);

		JwtToken jwtToken = response.token();
		MemberClaim memberClaim = jwtTokenProvider.extractMemberClaim(jwtToken.accessToken());
		// then
		assertThat(achievementAssociateRepository.existsByAchievementIdAndAssociateId(22L, memberClaim.associateId()))
			.isTrue();
	}

	@Test
	@DisplayName("홈스윗홈")
	void createTest() {
		// given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate associate = AssociateFixtures.associate(member, community);
		associateRepository.save(associate);

		associateStatsRepository.save(AssociateStats.builder()
			.associate(associate)
			.build());

		// when
		achievementService.create(associate.getId(), "HOME");

		// then
		assertThat(achievementAssociateRepository.existsByAchievementIdAndAssociateId(15L, associate.getId()))
			.isTrue();
	}
}
