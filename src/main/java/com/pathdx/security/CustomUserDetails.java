package com.pathdx.security;

import com.pathdx.model.Role;
import com.pathdx.model.UserModel;
import com.pathdx.repository.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@Profile("!local")

public class CustomUserDetails implements UserDetailsService {


    @Autowired
    UsersRepository usersRepository ;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading User with email "+username);
        Optional<UserModel> userModel = usersRepository.findUserModelByEmail(username);
        List<Role>  roles = userModel.get().getRoles();
        List<SimpleGrantedAuthority> auths = new ArrayList<>();
        if(roles.size()>0){
            addRoles(roles,auths);
        }
        if(userModel.isPresent()){
            UserModel user  = userModel.get();
            if(Objects.isNull(user.getPassword())){
                user.setPassword("sso" + user.getEmail());
            }
            return new User(user.getEmail(),user.getPassword(),auths);
        }
        throw new UsernameNotFoundException("No User Found");
    }

    private void addRoles( List<Role>  roles, List<SimpleGrantedAuthority> auths) {
        String roleData = "ROLE_";
        String role;
        for (int i = 0; i < roles.size(); i++){
            role = roleData+roles.get(i).getRoleName().toUpperCase();
            auths.add(new SimpleGrantedAuthority(role));
        }
    }


}
