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

    private Long id;
    private String title;
    private String body;
    private Set<CommentDTO> comments;
    private Set<HistoryDTO> history;

    public PostDTO(Post post){
        this.id = post.getId();
        this.title = post.getTitle();
        this.body = post.getBody();
        this.comments = new HashSet<>(post.getComments().stream().map(CommentDTO::new).toList());
        this.history = new HashSet<>(post.getHistory().stream().map(HistoryDTO::new).toList());
    }
}
