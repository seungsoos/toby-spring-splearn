package tobyspring.splearn.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberTest {

    Member member;
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        this.passwordEncoder = new PasswordEncoder() {
            @Override
            public String encode(String password) {
                return password.toUpperCase();
            }

            @Override
            public boolean matches(String password, String passwordHash) {
                return encode(password).equals(passwordHash);
            }
        };

        member = Member.create(new MemberCreateRequest("test@naver.com", "테스트", "secret"), passwordEncoder);
    }

    @Test
    @DisplayName("멤버 생성")
    void createMember() {
        // given
        // when

        // then
        assertThat(member.getStatus()).isEqualTo(MemberStatus.PENDING);
    }

    @Test
    void activate() {
        // given
        // when
        member.activate();
        // then
        assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
    }

    @Test
    void activateFail() {
        // given
        // when
        member.activate();
        // then
        assertThatThrownBy(() -> member.activate())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void deActivate() {
        // given
        member.activate();
        // when
        member.deActivate();
        // then
        assertThat(member.getStatus()).isEqualTo(MemberStatus.DEACTIVATED);
    }

    @Test
    void deActivateFail() {
        // given
        // when
        // then
        assertThatThrownBy(member::deActivate).isInstanceOf(IllegalStateException.class);

        member.activate();
        member.deActivate();
        assertThatThrownBy(member::deActivate).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void verifyPassword() {
        assertThat(member.verifyPassword("secret", passwordEncoder)).isTrue();
        assertThat(member.verifyPassword("spring", passwordEncoder)).isFalse();
    }

    @Test
    void invalidEmail() {
        // given
        assertThatThrownBy(
                () -> Member.create(new MemberCreateRequest("invalid email", "test", "secret"), passwordEncoder))
                 .isInstanceOf(IllegalArgumentException.class);

        Member.create(new MemberCreateRequest("test@gmail.com", "test", "secret"), passwordEncoder);
    }

}