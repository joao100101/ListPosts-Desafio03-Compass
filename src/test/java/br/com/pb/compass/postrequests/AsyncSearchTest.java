package br.com.pb.compass.postrequests;

import br.com.pb.compass.model.Post;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.*;

public class AsyncSearchTest {
    private static final ExecutorService threadpool =
            Executors.newFixedThreadPool(1);
    private static RestTemplate rest = new RestTemplate();
    public static Queue<Long> queue = new LinkedList<>();

    public static void main(String[] args) {

        Post p = new Post();
        p.setTitle("Normal title");

        System.out.println(p.getTitle());
        changeAt(p);
        System.out.println(p.getTitle());

//        System.out.println("Fila iniciada: " + new Date());
//
//
//        for (long i = 1L; i <= 100L; i++) {
//            addToQueue(i);
//        }
//        addToQueue(101L);


    }

    public static void changeAt(Post p){
        p.setTitle("alterado");
    }

    public static void addToQueue(Long id) {
        ProccessQueue proccessQueue = new ProccessQueue(id);
        Future<List<Post>> future = threadpool.submit(proccessQueue);
        System.out.println("ID: " + id + ", adicionado na fila as: " + new Date());
    }


    public static class ProccessQueue implements Callable<List<Post>> {

        public static Queue<Long> queue = new LinkedList<>();
        private final List<Post> processedPosts = new ArrayList<>();

        public ProccessQueue(Long id) {
            queue.add(id);
        }

        @Override
        public List<Post> call() {
            System.out.println("Iniciando processamento da fila. Data: " + new Date());
            Post post;

            for (long l : queue) {
                post = getPostAsync(l);
                if (post != null) {
                    if (!processedPosts.contains(post)) {
                        processedPosts.add(post);
                    }
                }
                queue.remove(l);
            }
            return processedPosts;
        }

        public static Post getPostAsync(Long id) {
            try {
                ResponseEntity<Post> response = rest.exchange(
                        "https://jsonplaceholder.typicode.com/posts/" + id,
                        HttpMethod.GET,
                        null,
                        Post.class
                );

                if (response.getStatusCode() == HttpStatus.OK) {
                    Post body = response.getBody();
                    System.out.println("Encontrado ID: " + id + ", horario: " + new Date());
                    return body;
                } else {
                    System.err.println("Falha ao recuperar id: " + id + ", horario: " + new Date());
                }
            } catch (HttpClientErrorException e) {
                System.err.println("Falha ao recuperar id: " + id + ", horario: " + new Date());
                System.err.println(e.getStatusCode());
            }

            return null;
        }
    }
}
