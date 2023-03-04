package monster.shop.controller;

import monster.shop.domain.dto.MemberFormDto;
import monster.shop.domain.model.Member;
import monster.shop.domain.service.MemberService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class MemberControllerTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    PasswordEncoder passwordEncoder;


    public Member createMember(String email, String password) {
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setEmail(email);
        memberFormDto.setName("윤일");
        memberFormDto.setAddress("경기도 김포시 걸포동");
        memberFormDto.setPassword(password);

        Member member = Member.createMember(memberFormDto, passwordEncoder);
        return memberService.saveMember(member);
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void loginSuccessTest() throws Exception {
        String email = "tingcobell@gmail.com";
        String password = "1234";
        Member member = this.createMember(email, password);
        assertThat(member).isNotNull();

        mockMvc.perform(formLogin()
                        .userParameter("email")
                        .loginProcessingUrl("/members/login")
                        .user(email)
                        .password(password))
                .andExpect(SecurityMockMvcResultMatchers
                        .authenticated());
    }

    @Test
    @DisplayName("로그인 실패 테스트")
    void loginFailTest() throws Exception {
        String email = "tingcobell@gmail.com";
        String password = "1234";
        Member member = this.createMember(email, password);
        assertThat(member).isNotNull();

        mockMvc.perform(formLogin()
                        .userParameter("email")
                        .loginProcessingUrl("/members/login")
                        .user(email)
                        .password("123456"))
                .andExpect(SecurityMockMvcResultMatchers
                        .unauthenticated());
    }
}