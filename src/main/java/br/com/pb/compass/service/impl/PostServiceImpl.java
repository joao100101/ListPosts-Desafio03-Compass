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
            Executors.newFixedThreadPool(5);

    @Autowired
    public PostServiceImpl(PostRepository repository) {
        this.repository = repository;
    }

    @Override
    public void addToQueue(Long id, Date requestTime) {
        if (this.repository.findById(id).isEmpty()) {
            ProcessQueue processQueue = new ProcessQueue(id);
            executeSearch(processQueue, requestTime);
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

    public void executeSearch(ProcessQueue processQueue, Date requestTime){
        new Thread(() ->{
            Future<List<Post>> future = threadpool.submit(processQueue);
            History created = new History(requestTime, br.com.pb.compass.model.State.CREATED);
            History find = new History(new Date(), br.com.pb.compass.model.State.POST_FIND);

            try {
                for (Post p : future.get()) {
                    created.setPost(p);
                    find.setPost(p);


                    p.addHistory(created);
                    p.addHistory(find);

                    save(p);
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @Override
    public List<PostDTO> findAll() {
        List<Post> posts = repository.findAll();

        return posts.stream().map(PostDTO::new).toList();
    }

}
