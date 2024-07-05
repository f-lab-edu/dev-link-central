package dev.linkcentral.service.security;

import dev.linkcentral.service.dto.token.MemberDetailsDTO;
import dev.linkcentral.service.facade.MemberFacade;
import dev.linkcentral.service.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * CustomUserDetailsService는 Spring Security에서 사용자 인증을 위해 사용되는 UserDetailsService를 구현한 서비스입니다.
 * 이메일을 통해 사용자를 로드하고, 해당 사용자의 인증 정보를 생성합니다.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberFacade memberFacade;
    private final MemberMapper memberMapper;

    /**
     * 이메일을 통해 사용자 정보를 로드합니다.
     *
     * @param email 사용자 이메일
     * @return UserDetails 사용자 인증 정보
     * @throws UsernameNotFoundException 사용자 정보가 없을 경우 예외 발생
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        MemberDetailsDTO memberDetailsDTO = memberFacade.findByEmailAndDeletedFalse(email);
        if (memberDetailsDTO == null) {
            throw new UsernameNotFoundException("사용자를 이메일로 찾을 수 없습니다: " + email);
        }
        return memberMapper.createUserDetails(memberDetailsDTO);
    }
}
