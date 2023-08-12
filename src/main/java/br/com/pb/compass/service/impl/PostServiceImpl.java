package br.com.pb.compass.service.impl;
import br.com.pb.compass.exception.PostAlreadyExistsException;
import br.com.pb.compass.exception.PostNotFoundException;
import br.com.pb.compass.model.History;
import br.com.pb.compass.model.Post;
import br.com.pb.compass.model.State;
import br.com.pb.compass.model.dto.PostDTO;
import br.com.pb.compass.queue.ProcessQueue;
import br.com.pb.compass.repository.PostRepository;
import br.com.pb.compass.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class PostServiceImpl implements PostService {

    private PostRepository repository;
    private static final ExecutorService threadpool =
            Executors.newFixedThreadPool(1);

    @Autowired
    public PostServiceImpl(PostRepository repository) {
        this.repository = repository;
    }

    @Override
    public void addToQueue(Long id, Date requestTime) {
        if (this.repository.findById(id).isEmpty()) {
            History created = new History(requestTime, State.CREATED);
            History find = new History(new Date(), State.POST_FIND);

            ProcessQueue processQueue = new ProcessQueue(id);
            Future<List<Post>> future = threadpool.submit(processQueue);

            System.out.println("ID: " + id + ", adicionado na fila as: " + new Date());
            try {
                for (Post p : future.get()) {
                    created.setPost(p);
                    find.setPost(p);

                    p.addHistory(created);
                    p.addHistory(find);

                    System.out.println("Salvando post id: " + p.getId());
                    save(p);

                }
            } catch (InterruptedException | ExecutionException e) {

                throw new RuntimeException(e);
            }
        } else {
            throw new PostAlreadyExistsException("Ja existe um post com o ID " + id);
        }
    }

    @Override
    public void save(Post post) {
        if (this.repository.findById(post.getId()).isEmpty()) {
            this.repository.save(post);
        } else {
            throw new PostAlreadyExistsException("Ja existe um post com o ID " + post.getId());
        }
    }

    @Override
    public List<PostDTO> findAll() {
        List<Post> posts = repository.findAll();

        return posts.stream().map(PostDTO::new).toList();
    }

    @Override
    public PostDTO findByID(Long id) {
        Post post = repository.findById(id).orElseThrow(() -> new PostNotFoundException("Nao encotrado post com id " + id));

        return new PostDTO(post);
    }
}
