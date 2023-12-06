package dev.linkcentral.database.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@ToString(of = {"id", "name", "password", "email", "nickname"})
@Entity
public class Member extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(length = 50, unique = true)
    private String name;

    @Setter
    @Column(length = 100, nullable = false)
    private String password;

    @Setter
    @Column(length = 100, nullable = false)
    private String email;

    @Setter
    @Column(length = 100, nullable = false)
    private String nickname;

    protected Member() {
    }
}