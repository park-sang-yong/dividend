package com.zerobase.dividend.service;

import com.zerobase.dividend.exception.impl.AlreadyExistUserException;
import com.zerobase.dividend.model.Auth;
import com.zerobase.dividend.persist.entity.MemberEntity;
import com.zerobase.dividend.persist.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class MemberService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("couldn't find user -> " + username));
    }

    public MemberEntity register(Auth.SignUp member) {
        // ID 생성 가능한 경우, 멤버 테이블에 저장
        boolean exists = this.memberRepository.existsByUsername(member.getUsername());
        // 아이디가 존재하는 경우 exception 발생
        if (exists) {
            throw new AlreadyExistUserException();
        }
        // 비밀번호는 암호화 되어서 저장되어야함
        member.setPassword(this.passwordEncoder.encode(member.getPassword()));
        var result = this.memberRepository.save(member.toEntity());
        return result;
    }

    public MemberEntity authenticate(Auth.SignIn member) {
//        // id 로 멤버 조회
        var user = this.memberRepository.findByUsername(member.getUsername())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 ID입니다."));
//        // 패스워드 일치 여부 확인
        if (!this.passwordEncoder.matches(member.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
//        //      - 일치하지 않는 경우 400 status 코드와 적합한 에러 메시지 반환
//        //      - 일치하는 경우, 해당 멤버 엔티티 반환
        return user;
    }
}
