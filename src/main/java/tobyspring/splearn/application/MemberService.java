package tobyspring.splearn.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tobyspring.splearn.application.required.EmailSender;
import tobyspring.splearn.application.provided.MemberRegister;
import tobyspring.splearn.application.required.MemberRepository;
import tobyspring.splearn.domain.*;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService implements MemberRegister {

    private final MemberRepository memberRepository;
    private final EmailSender emailSender;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Member register(MemberRegisterRequest registerRequest) {
        checkDuplicateEmail(registerRequest);

        Member member = Member.register(registerRequest, passwordEncoder);

        memberRepository.save(member);

        sendWelcomeEmail(member);
        return member;
    }

    private void sendWelcomeEmail(Member member) {
        emailSender.send(member.getEmail(), "등록을 완료해주세요", "링크 클릭");
    }

    private void checkDuplicateEmail(MemberRegisterRequest registerRequest) {
        if (memberRepository.findByEmail(new Email(registerRequest.email())).isPresent()) {
            throw new DuplicateEmailException("이미 사용중인 이메일입니다. " + registerRequest.email());
        }
    }
}
