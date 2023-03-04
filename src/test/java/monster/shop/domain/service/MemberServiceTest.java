package monster.shop.domain.service;

import lombok.extern.slf4j.Slf4j;
import monster.shop.domain.dto.MemberFormDto;
import monster.shop.domain.model.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@Slf4j
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    PasswordEncoder passwordEncoder;

    private Member createMember() {
        MemberFormDto dto = new MemberFormDto();
        dto.setEmail("tingcobell@nm-monster.com");
        dto.setName("윤일");
        dto.setAddress("서울시");
        dto.setPassword("12345678");
        return Member.createMember(dto, passwordEncoder);
    }

    @Test
    @DisplayName("회원가입 테스트")
    void saveMemberTest() {
        Member member = createMember();
        Member saveMember = memberService.saveMember(member);

        assertThat(member).isNotNull();
        assertThat(saveMember).isNotNull();
        assertThat(member.getEmail()).isEqualTo(saveMember.getEmail());
        assertThat(member.getName()).isEqualTo(saveMember.getName());
        assertThat(member.getAddress()).isEqualTo(saveMember.getAddress());
        assertThat(member.getPassword()).isEqualTo(saveMember.getPassword());
    }

    @Test
    @DisplayName("중복 회원가입 테스트")
    void saveDuplicationTest() {
        // given
        Member member1 = createMember();
        Member member2 = createMember();

        // when
        memberService.saveMember(member1);
        Throwable throwable = catchThrowable(() -> memberService.saveMember(member2));

        // then
        assertThat(throwable)
                .hasMessage("이미 가입된 유저입니다.")
                .isInstanceOf(IllegalStateException.class);

    }
}