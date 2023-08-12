package br.com.pb.compass.model.dto;

import br.com.pb.compass.model.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {

    private Long userId;
    private Long id;
    private String title;
    private String body;
    private Set<HistoryDTO> history;

    public PostDTO(Post post){
        this.userId = post.getUserId();
        this.id = post.getId();
        this.title = post.getTitle();
        this.body = post.getBody();
        this.history = new HashSet<>(post.getHistory().stream().map(HistoryDTO::new).toList());
    }
}
