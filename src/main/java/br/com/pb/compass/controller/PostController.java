package br.com.pb.compass.controller;

import br.com.pb.compass.model.dto.PostDTO;
import br.com.pb.compass.service.impl.PostServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    private PostServiceImpl postService;

    @Autowired
    public PostController(PostServiceImpl postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<List<PostDTO>> getAllAfterProccess(){

        return ResponseEntity.ok(postService.findAll());
    }


    @PostMapping("/{id}")
    public ResponseEntity<String> getPostFromExternalAPI(@PathVariable Long id){
        if(id != null){
            if(id > 0 && id <= 100){
                postService.addToSearchQueue(id);
            }else{
                return ResponseEntity.ok("O id deve ser um valor entre 1 e 100");
            }
        }else{
            return ResponseEntity.ok("O Valor inserido nao deve ser nulo");
        }
        return ResponseEntity.ok("Procurando Post id: " + id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updatePost(@PathVariable Long id){
        postService.addToUpdateQueue(id);

        return ResponseEntity.ok("Atualizando Post id: " + id);
    }





}
