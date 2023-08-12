package br.com.pb.compass.controller;

import br.com.pb.compass.model.dto.PostDTO;
import br.com.pb.compass.service.impl.PostServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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
    public List<PostDTO> getAllAfterProccess(){

        return postService.findAll();
    }


    @PostMapping("/{id}")
    public String getPostFromExternalAPI(@PathVariable Long id){


        postService.addToQueue(id, new Date());

        return "Procurando Post id: " + id + "\n" + new Date();
    }





}
