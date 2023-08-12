package br.com.pb.compass.model.dto;


import br.com.pb.compass.model.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {

    private Long id;
    private String body;

    public CommentDTO(Comment comment) {
        this.id = comment.getId();
        this.body = comment.getBody();
    }
}
