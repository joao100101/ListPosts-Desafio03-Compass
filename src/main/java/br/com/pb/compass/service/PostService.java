package br.com.pb.compass.service;

import br.com.pb.compass.model.Post;
import br.com.pb.compass.model.dto.PostDTO;

import java.util.List;

public interface PostService {
    void addToSearchQueue(Long id);
    void save(Post post);
    void update(Post post);
    List<PostDTO> findAll();
    void disablePost(Long postID);
    void addToUpdateQueue(Long postID);
}

