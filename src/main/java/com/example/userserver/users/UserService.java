package com.example.userserver.users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.client.RestClient;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class UserService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UserRepository userRepository;
    @Value("${sns.post-server}")
    private String postServerUrl;
    private final RestClient restClient = RestClient.create();
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserInfo createUser(UserRequest userRequest) {

        String hashedPassword = passwordEncoder.encode(userRequest.getPlainPassword());
        if (userRepository.findByUsername(userRequest.getUsername()) != null) {
            throw new RuntimeException("Username duplicated");
        }

        User user = new User(userRequest.getUsername(), userRequest.getEmail(), hashedPassword);
        User savedUser = userRepository.save(user);

        return new UserInfo(savedUser);
    }

    public UserInfo getUser(int userId) {
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return null;
        }

        return new UserInfo(user);
    }

    public List<UserInfo> getAllUser() {
        return userRepository.findAll().stream().map(UserInfo::new).toList();
    }

    public UserInfo getUserByName(String name) {
        User user = userRepository.findByUsername(name);
        if (user == null) {
            return null;
        }

        return new UserInfo(user);
    }

    public UserInfo signIn(UserRequest signInRequest) {
        User user = null;
        if (signInRequest.getUsername() != null) {
            user = userRepository.findByUsername(signInRequest.getUsername());
        }

        if (user == null) {
            return null;
        }

        boolean isPasswordMatch = passwordEncoder.matches(signInRequest.getPlainPassword(), user.getPassword());
        if (isPasswordMatch) {
            return new UserInfo(user);
        }

        return null;
    }

    public void updateLastPost(int userId, String postId, ZonedDateTime updatedDatetime) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setLastPostId(postId);
        user.setLastPostDatetime(updatedDatetime);
        userRepository.save(user);
    }

    @Transactional
    public boolean deleteUser(int id) {
        Boolean deactivated = false;
        try {
            deactivated = restClient.delete()
                    .uri(postServerUrl + "/api/posts/deactivate/" + id)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        throw new RuntimeException("invalid server response " + response.getStatusText());
                    })
                    .body(Boolean.class);

            if (deactivated) {
                userRepository.deleteById(id);
                Thread.sleep(5000);
                throw new RuntimeException("Fake Exception to force rollback");
            } else {
                return false;
            }
        } catch(Exception e) {
            try {
                Boolean reactivated = restClient.put()
                        .uri(postServerUrl + "/api/posts/activate/" + id)
                        .retrieve()
                        .onStatus(HttpStatusCode::isError, (request, response) -> {
                            throw new RuntimeException("Failed to reactivate posts: " + response.getStatusText());
                        })
                        .body(Boolean.class);

                if (!reactivated) {
                    // Log the failure to reactivate posts
                    logger.error("Failed to reactivate posts for user " + id + " after deletion failure");
                }
            } catch (Exception reactivateException) {
                logger.error("Failed to reactivate posts for user " + id + " after deletion failure");
            }

            return false;
        }

//        return true;
    }

}
