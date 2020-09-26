package com.andrew121410.genzprofessor.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GenZProfessorJacksonConfig {
    @JsonProperty("Discord-Token")
    private String token = "bot-token";

    @JsonProperty("Command-Prefix")
    private String prefix = "!";

    @JsonProperty("Chegg-Email")
    private String cheggEmail = "email";

    @JsonProperty("Chegg-Password")
    private String cheggPassword = "password";
}