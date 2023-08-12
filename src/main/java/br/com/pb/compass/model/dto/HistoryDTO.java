package br.com.pb.compass.model.dto;

import br.com.pb.compass.model.History;
import br.com.pb.compass.model.State;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HistoryDTO {

    private Long id;
    private Date date;
    private State state;

    public HistoryDTO(History history){
        this.id = history.getId();
        this.date = history.getDate();
        this.state = history.getState();
    }

}
