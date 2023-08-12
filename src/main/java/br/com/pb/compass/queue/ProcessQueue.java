package br.com.pb.compass.queue;

import br.com.pb.compass.model.Comment;
import br.com.pb.compass.model.History;
import br.com.pb.compass.model.Post;
import br.com.pb.compass.model.State;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.Callable;

public class ProcessQueue implements Callable<List<Post>> {

    private final Queue<Long> queue = new LinkedList<>();
    private final List<Post> processedPosts = new ArrayList<>();
    private Set<History> histories = new HashSet<>();
    private final RestTemplate rest = new RestTemplate();

    public ProcessQueue(Long id) {
        queue.add(id);
    }

    @Override
    public List<Post> call() {
        Post post;

        for (long l : queue) {
            post = new Post();
            post.setId(l);
            post = getPostAsync(post);

            if (!processedPosts.contains(post)) {
                getCommentsAsync(post);
                post.setHistory(histories);
                processedPosts.add(post);
            }
            queue.remove(l);
        }
        return processedPosts;
    }

    public Post getPostAsync(Post post) {
        Long id = post.getId();
        try {
            ResponseEntity<Post> response = rest.exchange(
                    "https://jsonplaceholder.typicode.com/posts/" + id,
                    HttpMethod.GET,
                    null,
                    Post.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                post = response.getBody();
                if (post == null) {
                    post = new Post();
                }

                addHistory(post, State.POST_OK);

                post.setId(id);
                return post;
            } else {
                addHistory(post, State.FAILED);
                addHistory(post, State.DISABLED);
                post.setId(id);
                return post;
            }
        } catch (HttpClientErrorException e) {
            post = new Post();
            addHistory(post, State.FAILED);
            addHistory(post, State.DISABLED);
            post.setId(id);
            return post;
        }
    }

    public void getCommentsAsync(Post post) {
        Long id = post.getId();

        addHistory(post, State.COMMENTS_FIND);

        try {
            ResponseEntity<Comment[]> response = rest.exchange(
                    "https://jsonplaceholder.typicode.com/posts/" + post.getId() + "/comments",
                    HttpMethod.GET,
                    null,
                    Comment[].class
            );


            if (response.getStatusCode() == HttpStatus.OK) {

                Set<Comment> comments = new HashSet<>(Arrays.asList(response.getBody()));


                for (Comment comment : comments) {
                        Comment c1 = new Comment(comment.getBody(), post);
                        post.getComments().add(c1);
                }


                addHistory(post, State.COMMENTS_OK);
                addHistory(post, State.ENABLED);

                post.setId(id);
            } else {
                addHistory(post, State.FAILED);
                addHistory(post, State.DISABLED);
            }
        } catch (HttpClientErrorException e) {
            addHistory(post, State.FAILED);
            addHistory(post, State.DISABLED);
        }
    }

    public void addHistory(Post post, State state){
        History hist = new History(new Date(), state, post);
        histories.add(hist);
    }
}
