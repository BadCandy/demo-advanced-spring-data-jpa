package study.datajpa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

@Transactional
@Rollback(false)
@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    public void testMember() {

        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);

        System.out.println(member);
        System.out.println(findMember);
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        List<Member> all = memberRepository.findAll();
        long count = memberRepository.count();

        assertThat(all).hasSize(2);
        assertThat(all).hasSize((int) count);
    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {

        Member member1 = new Member("aaa", 10);
        Member member2 = new Member("aaa", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> members = memberRepository.findByUsernameAndAgeGreaterThan("aaa", 19);
        assertThat(members.get(0).getUsername()).isEqualTo("aaa");
        assertThat(members.get(0).getAge()).isEqualTo(20);

        assertThat(members).hasSize(1);
    }

    @Test
    public void testNamedQuery() {

        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> members = memberRepository.findByUsername("member1");
        Member findMember = members.get(0);
        assertThat(findMember).isEqualTo(member1);
    }

    @Test
    public void testQuery() {

        Member member1 = new Member("aaa", 10);
        Member member2 = new Member("aaa", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> members = memberRepository.findUser("aaa", 20);

        assertThat(members.get(0).getUsername()).isEqualTo("aaa");
        assertThat(members.get(0).getAge()).isEqualTo(20);
        assertThat(members).hasSize(1);
    }

    @Test
    public void findUsernameList() {

        Member member1 = new Member("aaa", 10);
        Member member2 = new Member("bbb", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<String> members = memberRepository.findUsernameList();

        members.forEach(System.out::println);
    }

    @Test
    public void findMemberDto() {

        Team team = new Team("teamName");
        teamRepository.save(team);

        Member member = new Member("aaa", 30, team);
        memberRepository.save(member);

        List<MemberDto> memberDtos = memberRepository.findMemberDto();
        memberDtos.forEach(System.out::println);
    }

    @Test
    public void findByNames() {

        Member member1 = new Member("aaa", 10);
        Member member2 = new Member("bbb", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> members = memberRepository.findByNames(Arrays.asList("aaa", "bbb"));

        members.forEach(System.out::println);
    }

    @Test
    public void returnType() {

        Member member1 = new Member("aaa", 10);
        Member member2 = new Member("bbb", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        Member member = memberRepository.findMemberByUsername("aaa");
        Optional<Member> optionalMember = memberRepository.findOptionalByUsername("aaa");
        List<Member> members = memberRepository.findListByUsername("aaa");
    }

    @Test
    public void paging() {

        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));
        memberRepository.save(new Member("member6", 10));

        int age = 10;
        int offset = 0;
        int limit = 3;

        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Direction.DESC, "username"));
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        Page<MemberDto> memberDto =
                page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        assertThat(content).hasSize(limit);
        assertThat(page.getTotalElements()).isEqualTo(6);
        assertThat(page.getNumber()).isEqualTo(offset);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.hasNext()).isTrue();
        assertThat(page.isFirst()).isTrue();

        for (Member member : content) {
            System.out.println("member = " + member);
        }

        System.out.println("totalCount = " + totalElements);
    }

//    @Test
//    public void slice() {
//
//        memberRepository.save(new Member("member1", 10));
//        memberRepository.save(new Member("member2", 10));
//        memberRepository.save(new Member("member3", 10));
//        memberRepository.save(new Member("member4", 10));
//        memberRepository.save(new Member("member5", 10));
//        memberRepository.save(new Member("member6", 10));
//
//        int age = 10;
//        int offset = 0;
//        int limit = 3;
//
//        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Direction.DESC, "username"));
//        Slice<Member> slice = memberRepository.findByAge(age, pageRequest);
//
//        List<Member> content = slice.getContent();
//
//        assertThat(content).hasSize(3);
//        assertThat(slice.getNumber()).isEqualTo(0);
//        assertThat(slice.hasNext()).isTrue();
//        assertThat(slice.isFirst()).isTrue();
//
//        for (Member member : content) {
//            System.out.println("member = " + member);
//        }
//    }

    @Test
    public void bulkUpdate() {

        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        int resultCount = memberRepository.bulkAgePlus(20);

//        em.flush();
//        em.clear();

        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);
        System.out.println("member5 = " + member5);

        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy() {

        // given
        // member1 -> teamA
        // member2 -> teamB
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);

        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        // when
        List<Member> members = memberRepository.findEntityGraphByUsername("member1");

        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.team = " + member.getTeam().getName());
        }
    }

    @Test
    public void queryHint() {

        // given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        // when
        Member findMember = memberRepository.findReadOnlyByUsername(member1.getUsername());
        findMember.setUsername("member2");

        em.flush();
    }

    @Test
    public void lock() {

        // given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        // when
        List<Member> result = memberRepository.findLockByUsername(member1.getUsername());

        em.flush();
    }

    @Test
    public void callCustom() {

        List<Member> memberCustom = memberRepository.findMemberCustom();
    }

    @Test
    public void queryByExample() {

        // given
        Team teamA = new Team("teamA");

        teamRepository.save(teamA);

        Member member1 = new Member("member1", 0, teamA);
        Member member2 = new Member("member2", 0, teamA);

        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        // when
        // Probe
        Member member = new Member("member1");
        Team team = new Team("teamA");
        member.setTeam(team);

        ExampleMatcher matcher = ExampleMatcher.matching()
                                               .withIgnorePaths("age");

        Example<Member> example = Example.of(member, matcher);

        List<Member> members = memberRepository.findAll(example);

        assertThat(members.get(0).getUsername()).isEqualTo("member1");
    }

    @Test
    public void projections() {

        // given
        Team teamA = new Team("teamA");

        teamRepository.save(teamA);

        Member member1 = new Member("member1", 0, teamA);
        Member member2 = new Member("member2", 0, teamA);

        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        // when
        List<NestedClosedProjections> result = memberRepository.findProjectionsByUsername("member1",
                                                                                          NestedClosedProjections.class);

        for (NestedClosedProjections nestedClosedProjections : result) {
            System.out.println("usernameOnly = " + nestedClosedProjections.getUsername());
            System.out.println("usernameOnly = " + nestedClosedProjections.getTeam().getName());

        }
    }

    @Test
    public void nativeQuery() {

        // given
        Team teamA = new Team("teamA");

        teamRepository.save(teamA);

        Member member1 = new Member("member1", 0, teamA);
        Member member2 = new Member("member2", 0, teamA);

        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        // when
        PageRequest pageRequest = PageRequest.of(1, 10);
        Page<MemberProjection> result = memberRepository.findByNativeProjection(pageRequest);
        List<MemberProjection> content = result.getContent();

        for (MemberProjection memberProjection : content) {
            System.out.println("memberProjection = " + memberProjection.getId());
            System.out.println("memberProjection = " + memberProjection.getUsername());
            System.out.println("memberProjection = " + memberProjection.getTeamName());
        }
        System.out.println(result);
    }
}