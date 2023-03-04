package monster.shop.domain.repository;

import lombok.extern.slf4j.Slf4j;
import monster.shop.domain.dto.MemberFormDto;
import monster.shop.domain.model.Cart;
import monster.shop.domain.model.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

@Slf4j
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class CartRepositoryTest {

    @Autowired
    CartRepository cartRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @PersistenceContext
    EntityManager em;

    public Member createMember(String email, String name, String address, String password) {
        MemberFormDto dto = new MemberFormDto();
        dto.setEmail(email);
        dto.setName(name);
        dto.setAddress(address);
        dto.setPassword(password);
        return Member.createMember(dto, passwordEncoder);
    }

    @Test
    @DisplayName("장바구니 회원 엔티티 매핑 조회 테스트")
    @Transactional
    void findCartAndMemberTest() {
        Member member = createMember("tingcobell@nm-monster.com", "윤일", "경기도 김포시 걸포동", "123456789");
        memberRepository.save(member);

        Cart cart = new Cart();
        cart.setMember(member);
        cartRepository.save(cart);

        em.flush();
        em.clear();

        Cart saveCart = cartRepository.findById(cart.getId())
                .orElseThrow(EntityNotFoundException::new);
        Assertions.assertThat(saveCart.getMember().getId()).isEqualTo(member.getId());
    }
}