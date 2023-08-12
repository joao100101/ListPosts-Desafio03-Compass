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
    private final RestTemplate rest = new RestTemplate();

    public ProcessQueue(Long id) {
        queue.add(id);
    }

    @Override
    public List<Post> call() {
        System.out.println("Iniciando processamento da fila. Data: " + new Date());
        Post post;

        for (long l : queue) {
            post = new Post();
            post.setId(l);
            post = getPostAsync(post);
            if (post != null) {
                if (!processedPosts.contains(post)) {
                    processedPosts.add(post);
                }
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
                System.out.println("Encontrado ID: " + id + ", horario: " + new Date());

                History ok = new History(new Date(), State.POST_OK);
                ok.setPost(post);
                post.addHistory(ok);

                post.setId(id);
                post = getCommentsAsync(post);
                return post;
            } else {
                History fail = new History(new Date(), State.FAILED);
                post.addHistory(new History(new Date(), State.DISABLED));
                fail.setPost(post);
                post.addHistory(fail);

                System.err.println("Falha ao recuperar id: " + id + ", horario: " + new Date());
                return post;
            }
        } catch (HttpClientErrorException e) {
            History fail = new History(new Date(), State.FAILED);
            fail.setPost(post);
            post.addHistory(fail);
            post.addHistory(new History(new Date(), State.DISABLED));

            System.err.println("Falha ao recuperar id: " + id + ", horario: " + new Date());
            System.err.println(e.getStatusCode());
            return post;
        }
    }

    public Post getCommentsAsync(Post post) {
        Long id = post.getId();
        History commentFind = new History(new Date(), State.COMMENTS_FIND);
        post.addHistory(commentFind);
        try {
            ResponseEntity<Comment[]> response = rest.exchange(
                    "https://jsonplaceholder.typicode.com/posts/" + post.getId() + "/comments",
                    HttpMethod.GET,
                    null,
                    Comment[].class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                Set<Comment> comments = new HashSet<>(Arrays.asList(response.getBody()));
                comments.forEach((comment) -> {
                    comment.setPost(post);
                });
                post.setComments(new HashSet<>(comments));
                System.out.println("Encontrado comentarios, horario: " + new Date());

                History ok = new History(new Date(), State.COMMENTS_OK);
                ok.setPost(post);
                post.addHistory(ok);
                post.addHistory(new History(new Date(), State.ENABLED));

                post.setId(id);
                return post;
            } else {
                History fail = new History(new Date(), State.FAILED);
                fail.setPost(post);
                post.addHistory(fail);
                post.addHistory(new History(new Date(), State.DISABLED));

                System.err.println("Falha ao recuperar id: " + id + ", horario: " + new Date());
                return post;
            }
        } catch (HttpClientErrorException e) {
            History fail = new History(new Date(), State.FAILED);

            fail.setPost(post);
            post.addHistory(fail);
            post.addHistory(new History(new Date(), State.DISABLED));

            System.err.println("Falha ao recuperar id: " + id + ", horario: " + new Date());
            System.err.println(e.getStatusCode());
            return post;
        }
    }


}
