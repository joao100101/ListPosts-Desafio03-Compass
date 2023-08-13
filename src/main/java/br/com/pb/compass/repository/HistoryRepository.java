package br.com.pb.compass.repository;

import br.com.pb.compass.model.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {

    Set<History> findTop2ByPostIdOrderByDateDesc(Long postId);

}
