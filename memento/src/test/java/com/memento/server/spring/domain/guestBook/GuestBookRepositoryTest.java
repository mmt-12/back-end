package com.memento.server.spring.domain.guestBook;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.memento.server.associate.AssociateFixtures;
import com.memento.server.community.CommunityFixtures;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.community.Community;
import com.memento.server.domain.community.CommunityRepository;
import com.memento.server.domain.guestBook.GuestBook;
import com.memento.server.domain.guestBook.GuestBookRepository;
import com.memento.server.domain.guestBook.GuestBookType;
import com.memento.server.domain.member.Member;
import com.memento.server.domain.member.MemberRepository;
import com.memento.server.member.MemberFixtures;
import com.memento.server.spring.api.service.IntegrationsTestSupport;

public class GuestBookRepositoryTest extends IntegrationsTestSupport {

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	CommunityRepository communityRepository;

	@Autowired
	AssociateRepository associateRepository;

	@Autowired
	GuestBookRepository guestBookRepository;

	@Test
	@DisplayName("방명록 페이지네이션 조회")
	void searchWithPaginationTest(){
		// given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate associate = AssociateFixtures.associate(member, community);
		associateRepository.save(associate);

		GuestBook guestBook1 = GuestBook.builder()
			.type(GuestBookType.TEXT)
			.content("test1")
			.associate(associate)
			.build();

		GuestBook guestBook2 = GuestBook.builder()
			.type(GuestBookType.TEXT)
			.content("test2")
			.associate(associate)
			.build();

		guestBookRepository.save(guestBook1);
		guestBookRepository.save(guestBook2);

		Pageable pageable = PageRequest.of(0,1);

		// when
		List<GuestBook> list = guestBookRepository.findPageByAssociateId(associate.getId(), null, pageable);

		// then
		assertThat(list.get(0).getId()).isEqualTo(guestBook2.getId());
	}

	@Test
	@DisplayName("방명록 페이지네이션 커서값 조회")
	void searchWithPaginationAndCursorTest(){
		// given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate associate = AssociateFixtures.associate(member, community);
		associateRepository.save(associate);

		GuestBook guestBook1 = GuestBook.builder()
			.type(GuestBookType.TEXT)
			.content("test1")
			.associate(associate)
			.build();

		GuestBook guestBook2 = GuestBook.builder()
			.type(GuestBookType.TEXT)
			.content("test2")
			.associate(associate)
			.build();

		guestBookRepository.save(guestBook1);
		guestBookRepository.save(guestBook2);

		Pageable pageable = PageRequest.of(0,1);

		// when
		List<GuestBook> list = guestBookRepository.findPageByAssociateId(associate.getId(),  guestBook2.getId(), pageable);

		// then
		assertThat(list.get(0).getId()).isEqualTo(guestBook2.getId());
	}

}
