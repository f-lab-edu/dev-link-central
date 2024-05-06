package dev.linkcentral.service.security;

import dev.linkcentral.service.dto.token.MemberDetailsDTO;
import dev.linkcentral.service.facade.MemberFacade;
import dev.linkcentral.service.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberFacade memberFacade;
    private final MemberMapper memberMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        MemberDetailsDTO memberDetailsDTO = memberFacade.findByEmailAndDeletedFalse(email);
        if (memberDetailsDTO == null) {
            throw new UsernameNotFoundException("사용자를 이메일로 찾을 수 없습니다: " + email);
        }
        return memberMapper.createUserDetails(memberDetailsDTO);
    }

}
