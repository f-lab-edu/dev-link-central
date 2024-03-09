package dev.linkcentral.database.entity;

public enum FriendStatus {
    REQUESTED, // 받은 쪽에서 이 요청을 수락하거나 거절할 수 있는 상태를 의미
    ACCEPTED   // 수락됨 상태를 나타내며, 두 사용자가 서로 친구 관계를 의미
}
