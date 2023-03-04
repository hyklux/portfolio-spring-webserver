package monster.shop.domain.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import monster.shop.domain.constant.Role;
import monster.shop.domain.dto.MemberFormDto;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "member", uniqueConstraints = {@UniqueConstraint(name = "EMAIL_UNIQUE_KEY", columnNames = {"EMAIL"})})
@Getter
@Setter
@ToString
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String address;

    @Enumerated(EnumType.STRING)
    private Role role;

    public static Member createMember(MemberFormDto dto, PasswordEncoder passwordEncoder) {
        Member member = new Member();
        member.setName(dto.getName());
        member.setEmail(dto.getEmail());
        member.setAddress(dto.getAddress());
        String password = passwordEncoder.encode(dto.getPassword());
        member.setPassword(password);
        member.setRole(Role.ADMIN);
        return member;
    }

}
