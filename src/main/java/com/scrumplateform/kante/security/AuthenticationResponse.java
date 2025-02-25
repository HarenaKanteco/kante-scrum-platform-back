package com.scrumplateform.kante.security;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {

    private String token;
    private String type = "Bearer";
    private String id;
    private String email;
    private String photoUrl;
}
