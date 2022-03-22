package com.udacity.jwdnd.course1.cloudstorage.services;


import com.udacity.jwdnd.course1.cloudstorage.mapper.UserMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Base64;

@Service
public class UserAuthenticationService implements AuthenticationProvider {
    private UserMapper userMapper;

    public UserAuthenticationService(UserMapper userMapper) {
        this.userMapper = userMapper;

    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        /**get the user */
        /**  Get authentication if credentials matches all the requirements like username and passWord
         */
        User user = userMapper.getUser(username);
        if (user != null) {
            String encodedSalt = user.getSalt();
            String hashedPassword = getHashedValue(password, encodedSalt);
            if (user.getPassword().equals(hashedPassword)) {
                return new UsernamePasswordAuthenticationToken(username, password, new ArrayList<>());

            }
        }

        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

        private Logger logger = LoggerFactory.getLogger(getClass());

        public String getHashedValue(String data, String salt) {
            byte[] hashedValue = null;

            KeySpec spec = new PBEKeySpec(data.toCharArray(), salt.getBytes(), 5000, 128);
            try {
                /**From udacity */
                SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
                hashedValue = factory.generateSecret(spec).getEncoded();
            } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                logger.error(e.getMessage());
            }

            return Base64.getEncoder().encodeToString(hashedValue);
        }

}
