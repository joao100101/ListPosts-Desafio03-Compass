package br.com.pb.compass.service.impl;

import br.com.pb.compass.model.History;
import br.com.pb.compass.model.State;
import br.com.pb.compass.repository.HistoryRepository;
import br.com.pb.compass.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class HistoryServiceImpl implements HistoryService {

    private HistoryRepository historyRepository;

    @Autowired
    public HistoryServiceImpl(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    @Override
    public Set<History> findLatestHistories(Long postId) {
        return this.historyRepository.findTop2ByPostIdOrderByDateDesc(postId);
    }

    @Override
    public boolean isEnable(Long postId) {
        Set<History> historyDTOSet = findLatestHistories(postId);
        boolean isEnable = false;
        for(History h : historyDTOSet){
            if(h.getState() == State.ENABLED) {
                isEnable = true;
            }
            if(h.getState() == State.DISABLED){
                isEnable = false;
            }
        }
        return isEnable;
    }
}
