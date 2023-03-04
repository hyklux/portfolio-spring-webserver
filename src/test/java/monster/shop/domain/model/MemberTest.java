package monster.shop.domain.model;

import monster.shop.domain.dto.MemberFormDto;
import monster.shop.domain.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class MemberTest {

    @Autowired
    MemberRepository memberRepository;

    @PersistenceContext
    EntityManager em;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Member Auditing 테스트")
    @WithMockUser(username = "tingcobell", roles = "USER")
    void auditingTest() {

        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setEmail("tingcobell@gmail.com");
        memberFormDto.setName("홍길동");
        memberFormDto.setAddress("경기도 김포시 걸포동");
        memberFormDto.setPassword("123456789");

        Member newMember = Member.createMember(memberFormDto, passwordEncoder);

        memberRepository.save(newMember);

        em.flush();
        em.clear();

        Member findMember = memberRepository
                .findById(newMember.getId())
                .orElseThrow(EntityNotFoundException::new);

        assertThat(findMember.getId()).isEqualTo(newMember.getId());
        System.out.println("register time = " + findMember.getRegTime());
        System.out.println("update Time = " + findMember.getUpdateTime());
        System.out.println("create member = " + findMember.getCreatedBy());
        System.out.println("modify member = " + findMember.getModifiedBy());
    }

}