package br.com.pb.compass.service;

import br.com.pb.compass.model.History;

import java.util.Set;

public interface HistoryService {

    Set<History> findLatestHistories(Long postId);
    boolean isEnable(Long postId);
}
