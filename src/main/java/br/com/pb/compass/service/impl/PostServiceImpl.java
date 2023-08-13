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
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class PostServiceImpl implements PostService {
    private static final String POST_NOT_FOUND = "Post nao encontrado com o id: ";
    private static final String POST_ALREADY_EXISTS = "Ja existe um post com o id: ";

    private PostRepository repository;
    private static final ExecutorService threadpool =
            Executors.newFixedThreadPool(5);

    private Future<List<Post>> future;

    @Autowired
    public PostServiceImpl(PostRepository repository) {
        this.repository = repository;
    }

    @Override
    public void addToSearchQueue(Long id) {
        Date requestTime = new Date();
        if (this.repository.findById(id).isEmpty()) {
            ProcessQueue processQueue = new ProcessQueue(id);
            executeSearch(processQueue, requestTime);
        } else {
            throw new PostAlreadyExistsException(POST_ALREADY_EXISTS + id);
        }
    }

    @Override
    public void save(Post post) {
        if (this.repository.findById(post.getId()).isEmpty()) {
            this.repository.save(post);
        } else {
            throw new PostAlreadyExistsException(POST_ALREADY_EXISTS + post.getId());
        }
    }

    @Override
    public void update(Post post) {
        this.repository.save(post);
    }

    public void executeSearch(ProcessQueue processQueue, Date requestTime){
        new Thread(() ->{
            future = threadpool.submit(processQueue);
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

    public void executeSearch(ProcessQueue processQueue, Date requestTime, Set<History> oldHistory){
        new Thread(() ->{
            future = threadpool.submit(processQueue);
            History created = new History(requestTime, State.UPDATING);
            History find = new History(new Date(), br.com.pb.compass.model.State.POST_FIND);

            try {
                for (Post p : future.get()) {
                    for(History h : oldHistory){
                        h.setId(null);
                        h.setPost(p);
                        p.addHistory(h);
                    }
                    created.setPost(p);
                    find.setPost(p);


                    p.addHistory(created);
                    p.addHistory(find);

                    update(p);
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

    @Override
    public Post findById(Long id) {
        return this.repository.findById(id).orElseThrow(() -> new PostNotFoundException(POST_NOT_FOUND + id));
    }

    @Override
    public void disablePost(Long postID) {

    }

    @Override
    public void addToUpdateQueue(Long postID) {
        Date requestTime = new Date();
        Post post = this.repository.findById(postID).orElseThrow(() -> new PostNotFoundException(POST_NOT_FOUND + postID));
        ProcessQueue processQueue = new ProcessQueue(postID);
        executeSearch(processQueue, requestTime, post.getHistory());
    }
}
