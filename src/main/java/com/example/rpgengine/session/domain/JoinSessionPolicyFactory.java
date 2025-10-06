package com.example.rpgengine.session.domain;

public class JoinSessionPolicyFactory {

    public static JoinPolicy createJoinPolicy(String inviteCode) {
        if (inviteCode == null || inviteCode.isBlank()) {
            return new RequestJoinPolicy();
        }
        return new AllowJoinPolicy(inviteCode);
    }
}
