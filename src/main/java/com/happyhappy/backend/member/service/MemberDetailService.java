package com.happyhappy.backend.member.service;

import com.happyhappy.backend.member.domain.Member;
import com.happyhappy.backend.member.dto.MemberDetails;
import com.happyhappy.backend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다." + username));

        if (!member.getIsActive()) {
            throw new UsernameNotFoundException("비활성화된 계정입니다.");
        }
        return new MemberDetails(
                member.getMemberId(),
                member.getUsername(),
                member.getPassword(),
                member.getAuthorities()
        );
    }
}
