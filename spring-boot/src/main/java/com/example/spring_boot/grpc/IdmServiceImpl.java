package com.example.spring_boot.grpc;

import com.example.spring_boot.model.User;
import com.example.spring_boot.repository.UserRepository;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@GrpcService
public class IdmServiceImpl extends IdmServiceGrpc.IdmServiceImplBase {

    private static final String SECRET_KEY = "am_o_cheie_mica_asa_si_asa_si_serverul_cam_pica_asa_si_asa";

    private static final Set<String> BLACKLIST = ConcurrentHashMap.newKeySet();


    private final UserRepository userRepository;

    public IdmServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public void authenticate(AuthRequest request, StreamObserver<AuthResponse> responseObserver) {
        String email = request.getUsername();
        String rawPassword = request.getPassword();

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            responseObserver.onError(new RuntimeException("Authentication failed: user not found."));
            return;
        }

        User user = optionalUser.get();

        if (!Objects.equals(user.getPassword(), rawPassword)) {
            responseObserver.onError(new RuntimeException("Authentication failed: invalid password."));
            return;
        }

        long now = System.currentTimeMillis();
        long oneHour = 3600000L;

        String jti = UUID.randomUUID().toString();
        String jwt = Jwts.builder().setIssuer("http://idm-service.local").setSubject(String.valueOf(user.getId())).claim("role", user.getRole()).claim("email", user.getEmail()).setId(jti).setIssuedAt(new Date(now)).setExpiration(new Date(now + oneHour)).signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256).compact();
        AuthResponse response = AuthResponse.newBuilder().setTokenValue(jwt).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void validateToken(TokenRequest request, StreamObserver<TokenValidationResponse> responseObserver) {
        String token = request.getTokenValue();
        TokenValidationResponse.Builder builder = TokenValidationResponse.newBuilder();

        if (BLACKLIST.contains(token)) {
            builder.setValid(false).setExpired(true).setSubject("").setRole("").setEmail("");
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
            return;
        }

        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8))).build().parseClaimsJws(token);

            Claims body = claimsJws.getBody();
            String subject = body.getSubject();
            String role = (String) body.get("role");
            String email = (String) body.get("email");

            Date expiration = body.getExpiration();
            Date now = new Date();
            boolean isExpired = expiration != null && expiration.before(now);

            if (isExpired) {
                BLACKLIST.add(token);
                builder.setValid(false).setExpired(true).setSubject("").setRole("").setEmail("");
            } else {
                builder.setValid(true).setExpired(false).setSubject(subject).setRole(role).setEmail(email);
            }

        } catch (JwtException ex) {
            BLACKLIST.add(token);
            builder.setValid(false).setExpired(true).setSubject("").setRole("").setEmail("");
        }

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }


    @Override
    public void destroyToken(TokenRequest request, StreamObserver<DestroyTokenResponse> responseObserver) {
        String token = request.getTokenValue();

        try {
            Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8))).build().parseClaimsJws(token);
        } catch (JwtException ex) {
        }

        BLACKLIST.add(token);

        DestroyTokenResponse response = DestroyTokenResponse.newBuilder().setSuccess(true).setMessage("Token destroyed").build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void createUser(UserRequest request, StreamObserver<UserResponse> responseObserver) {
        String token = request.getTokenValue();

        Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8))).build().parseClaimsJws(token);

        Claims claims = claimsJws.getBody();
        String role = (String) claims.get("role");

        if (!role.equalsIgnoreCase("ADMIN")) {
            responseObserver.onError(new RuntimeException("Must be admin to create users."));
            return;
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            responseObserver.onError(new RuntimeException("User with this email already exists."));
            return;
        }

        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setPassword(request.getPassword());
        newUser.setRole(request.getRole());

        User saved = userRepository.save(newUser);

        UserResponse userResp = UserResponse.newBuilder().setId(saved.getId()).setEmail(saved.getEmail()).setRole(saved.getRole()).build();

        responseObserver.onNext(userResp);
        responseObserver.onCompleted();
    }

    @Override
    public void getUserById(UserIdRequest request, StreamObserver<UserResponse> responseObserver) {
        long userId = request.getId();

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            responseObserver.onError(new RuntimeException("User not found with id: " + userId));
            return;
        }

        UserResponse userResp = UserResponse.newBuilder().setId(user.getId()).setEmail(user.getEmail()).setRole(user.getRole()).build();

        responseObserver.onNext(userResp);
        responseObserver.onCompleted();
    }


    @Override
    public void getAllUsers(Empty request, StreamObserver<UserList> responseObserver) {
        Iterable<User> all = userRepository.findAll();

        UserList.Builder listBuilder = UserList.newBuilder();

        for (User user : all) {
            UserResponse ur = UserResponse.newBuilder().setId(user.getId()).setEmail(user.getEmail()).setRole(user.getRole()).build();
            listBuilder.addUsers(ur);
        }

        responseObserver.onNext(listBuilder.build());
        responseObserver.onCompleted();
    }
}
