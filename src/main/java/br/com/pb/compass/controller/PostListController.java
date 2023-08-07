package br.com.pb.compass.controller;


import br.com.pb.compass.model.Post;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostListController {
    private final String EXTERNAL_POST_API_URL = "https://jsonplaceholder.typicode.com/posts";


    @GetMapping
    public ResponseEntity<List<Post>> listAll(){

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}")
    public ResponseEntity<String> postNewRequest(@PathVariable Long id){
//        RestTemplate rest = new RestTemplate();
//        rest.getForObject(EXTERNAL_POST_API_URL, Post.class);

        //na verdade deve adicionar a tarefa na fila para ser processada

        return ResponseEntity.ok("Pesquisando");
    }


}
