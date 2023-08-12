package br.com.pb.compass.service;

import br.com.pb.compass.model.Post;
import br.com.pb.compass.model.dto.PostDTO;

import java.util.Date;
import java.util.List;

public interface PostService {
    void addToQueue(Long id, Date requestTime);
    void save(Post post);
    List<PostDTO> findAll();
    PostDTO findByID(Long id);
}

