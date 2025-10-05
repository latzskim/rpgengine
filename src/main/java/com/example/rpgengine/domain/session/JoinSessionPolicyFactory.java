package com.example.rpgengine.domain.session;

public class JoinSessionPolicyFactory {

    public static JoinPolicy createJoinPolicy(String givenInviteCode) {
        if (givenInviteCode == null || givenInviteCode.isBlank()) {
            return new RequestJoinPolicy();
        }
        return new AllowJoinPolicy();
    }
}
