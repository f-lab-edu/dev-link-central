package dev.linkcentral.database.entity;

import dev.linkcentral.service.dto.request.ArticleSaveRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
@Builder
@AllArgsConstructor
public class Article extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(length = 50, nullable = false)
    private String writer;

    protected Article() {
    }


    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public static Article toSaveEntity(ArticleSaveRequestDTO articleSaveDTO) {
        return Article.builder()
                .title(articleSaveDTO.getTitle())
                .content(articleSaveDTO.getContent())
                .writer(articleSaveDTO.getWriter())
                .build();
    }
}