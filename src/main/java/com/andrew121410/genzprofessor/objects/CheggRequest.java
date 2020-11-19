package com.andrew121410.genzprofessor.objects;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public
class CheggRequest {
    private String guildId;
    private String userId;
    private String link;
}