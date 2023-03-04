package monster.shop.domain.repository;

import monster.shop.domain.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * email 정보로 맴버 검색 반환 함수.
     * @param email 맴버 이메일
     * @return 맴버 객체 반환.
     */
    Member findByEmail(String email);
}
