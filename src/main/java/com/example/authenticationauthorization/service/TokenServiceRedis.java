package com.example.authenticationauthorization.service;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.Key;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Slf4j
@Service
public class TokenServiceRedis {

    private final StringRedisTemplate redisTemplate;

    @Value("${jwt.expiration}")
    private long expirationTime;

    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    public TokenServiceRedis(  StringRedisTemplate redisTemplate) {

        this.redisTemplate = redisTemplate;

    }
    public Key getKey() {
        return key;
    }

    public String generateToken(Authentication authentication, String deviceId) {
        String username = authentication.getName();
        String newTokenVersion = UUID.randomUUID().toString();

        Set<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        Set<String> roles = authorities.stream()
                .filter(authority -> authority.startsWith("ROLE_")) // Filter roles
                .collect(Collectors.toSet());

        Set<String> permissions = authorities.stream()
                .filter(authority -> authority.startsWith("PERMISSION_")) // Filter permissions
                .collect(Collectors.toSet());

        String token = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(username)
                .claim("roles", roles)
                .claim("permissions", permissions)
                .claim("version", newTokenVersion)
                .claim("deviceId", deviceId)
                .setIssuer("com.AuthenticationAuthorization")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
        return token;
    }

    //sử duụng redis
    public boolean isLoggedInOnDifferentDevice(String username, String deviceId) {
        String keyPattern = "user:" + username + ":tokens:*";
        Set<String> keys = redisTemplate.keys(keyPattern);

        // Kiểm tra nếu có token trên một thiết bị khác (không phải deviceId hiện tại)
        if (keys != null) {
            for (String key : keys) {
                if (!key.equals("user:" + username + ":tokens:" + deviceId)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void saveToken(String username, String deviceId, String token, long expirationTimeInSeconds) {
        String key = "user:" + username + ":tokens:" + deviceId;

        redisTemplate.opsForValue().set(key, token, expirationTimeInSeconds, TimeUnit.SECONDS);

    }

    public String getToken(String username, String deviceId) {
        String key = "user:" + username + ":tokens:" + deviceId;
        return redisTemplate.opsForValue().get(key);
    }
    public List<String> getAllTokensForUsername(String username) {
        String keyPattern = "user:" + username + ":tokens:*";
        Set<String> keys = redisTemplate.keys(keyPattern);

        List<String> tokens = new ArrayList<>();
        if (keys != null) {
            for (String key : keys) {
                String token = redisTemplate.opsForValue().get(key);
                if (token != null) {
                    tokens.add(token);
                }
            }
        }

        return tokens;
    }


    public void deleteToken(String username, String deviceId) {
        String key = "user:" + username + ":tokens:" + deviceId;
        redisTemplate.delete(key);
    }
    public void deleteAllTokensForUser(String username) {
        // Get all devices for the user
        Set<String> deviceIds = redisTemplate.opsForSet().members("user:" + username + ":devices");

        if (deviceIds != null) {
            // Delete token for each device
            for (String deviceId : deviceIds) {
                String key = "user:" + username + ":tokens:" + deviceId;
                redisTemplate.delete(key);
            }

            // Optionally, clear the device set if no longer needed
            redisTemplate.delete("user:" + username + ":devices");
        }
    }
    //kiểm tra đăng nhập trùng lặp
    public boolean isLoggedIn(String username, String deviceId) {
        String token = getToken(username, deviceId);
        return token != null;
    }


    public long getExpirationTimeInSeconds(String token) {
        Date expiration = parseClaims(token).getExpiration();

        // Tính thời gian còn lại trước khi token hết hạn
        long expirationTimeInMillis = expiration.getTime();
        long currentTimeInMillis = System.currentTimeMillis();

        // Chuyển đổi thời gian còn lại từ milliseconds sang giây

        return (expirationTimeInMillis - currentTimeInMillis) / 1000;
    }


    public long getTokenTTL(String token) {
        // Key trong Redis có thể là token trực tiếp hoặc key nào đó liên quan đến token
        String key = "token:" + token;

        // Thực hiện lệnh getExpire để lấy TTL của token
        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);

        // Nếu TTL là -1 hoặc null, nghĩa là token không có trong Redis hoặc không có TTL, ta trả về 0
        if (ttl == null || ttl == -1) {
            return 0;
        }

        return ttl;
    }

    public boolean isTokenBlacklisted(String token) {
        // Xây dựng khóa Redis cho token trong danh sách đen
        String key = "blacklist:" + token;

        // Kiểm tra sự tồn tại của khóa trong Redis
        Boolean isBlacklisted = redisTemplate.hasKey(key);

        // Nếu khóa tồn tại, token đã bị thêm vào danh sách đen
        return Boolean.TRUE.equals(isBlacklisted);
    }

    public void addToBlacklist(String token, long ttl) {
        // Key để lưu token trong danh sách đen
        String blacklistKey = "blacklist:" + token;

        // Lưu token vào Redis với TTL xác định
        redisTemplate.opsForValue().set(blacklistKey, "BLACKLISTED", ttl, TimeUnit.SECONDS);
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (MalformedJwtException e) {
            // Xử lý lỗi định dạng JWT

            throw new RuntimeException("Malformed JWT token");
        } catch (Exception e) {
            // Xử lý lỗi khác
            throw new RuntimeException("Token parsing error", e);
        }
    }



}
