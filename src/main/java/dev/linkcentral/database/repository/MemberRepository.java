package dev.linkcentral.database.repository;

import dev.linkcentral.database.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
