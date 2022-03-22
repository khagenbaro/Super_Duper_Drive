package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.UserMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

@Service
public class UserService {

    private final UserMapper userMapper;

    public UserService(UserMapper userMapper ) {
        this.userMapper = userMapper;

    }

    public boolean isUsernameAvailable(String username) {
        return userMapper.getUser(username) == null;
    }

    public int createUser(User user) {
        /**From udacity course*/
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        String encodedSalt = Base64.getEncoder().encodeToString(salt);
        String hashedPassword = getHashedValue(user.getPassword(), encodedSalt);
        return userMapper.insert(new User(null, user.getUserame(), encodedSalt, hashedPassword, user.getFirstName(), user.getLastName()));
    }

    public User getUser(String username) {
        return userMapper.getUser(username);
    }

        private Logger logger = LoggerFactory.getLogger(getClass());

        public String getHashedValue(String data, String salt) {
            /**From stackoverflow*/

            byte[] hashedValue = null;

            KeySpec spec = new PBEKeySpec(data.toCharArray(), salt.getBytes(), 5000, 128);
            try {
                SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
                hashedValue = factory.generateSecret(spec).getEncoded();
            } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                logger.error(e.getMessage());
            }

            return Base64.getEncoder().encodeToString(hashedValue);
        }

}

