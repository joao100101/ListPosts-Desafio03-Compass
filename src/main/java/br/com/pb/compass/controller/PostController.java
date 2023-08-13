package br.com.pb.compass.controller;

import br.com.pb.compass.exception.PostNotFoundException;
import br.com.pb.compass.model.History;
import br.com.pb.compass.model.Post;
import br.com.pb.compass.model.State;
import br.com.pb.compass.model.dto.PostDTO;
import br.com.pb.compass.service.impl.HistoryServiceImpl;
import br.com.pb.compass.service.impl.PostServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    private PostServiceImpl postService;
    private HistoryServiceImpl historyService;
    @Autowired
    public PostController(PostServiceImpl postService, HistoryServiceImpl historyServicei) {
        this.postService = postService;
        this.historyService = historyServicei;
    }

    @GetMapping
    public ResponseEntity<List<PostDTO>> getAllAfterProccess(){

        return ResponseEntity.ok(postService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> disablePost(@PathVariable Long id){

        if(id < 1 || id > 100){
            throw new PostNotFoundException("ID: " + id + " inválido. Use um ID entre 1 e 100");
        }

        if(historyService.isEnable(id)){
            Post post = postService.findById(id);
            History disabled = new History(new Date(), State.DISABLED, post);
            post.addHistory(disabled);
            postService.update(post);
            return ResponseEntity.ok("Post id " + id  + " desativado.");
        }else{
            return new ResponseEntity<>("O Post deve estar ativado para poder ser desativado", HttpStatus.NOT_MODIFIED);
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<String> getPostFromExternalAPI(@PathVariable Long id){
        if(id > 0 && id <= 100){
            postService.addToSearchQueue(id);
        }else{
            throw new PostNotFoundException("ID: " + id + " inválido. Use um ID entre 1 e 100");
        }
        return ResponseEntity.ok("Procurando Post id: " + id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updatePost(@PathVariable Long id){
        if(id < 1 || id > 100){
            throw new PostNotFoundException("ID: " + id + " inválido. Use um ID entre 1 e 100");
        }
        postService.addToUpdateQueue(id);

        return ResponseEntity.ok("Atualizando Post id: " + id);
    }

}
