package com.happyhappy.backend.member.repository;

import com.happyhappy.backend.member.domain.Member;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, UUID> {

    Optional<Member> findById(UUID memberId);

    Optional<Member> findByUserid(String userid);

    Optional<Member> findByUsername(String username);

    boolean existsByUserId(String userId);

    boolean existsByUsername(String username);
}
