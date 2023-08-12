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
        if(id != null){
            if(id > 0 && id <= 100){
                postService.addToQueue(id, new Date());
            }else{
                return "O id deve ser um valor entre 1 e 100";
            }
        }else{
            return "O Valor inserido nao deve ser nulo";
        }

        return "Procurando Post id: " + id + "\n" + new Date();
    }





}
