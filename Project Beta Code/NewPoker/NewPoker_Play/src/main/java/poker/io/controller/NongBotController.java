package poker.io.controller;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poker.io.repository.NongBot;
import poker.io.repository.NongBotMapper;

import java.util.List;

@Service
//@Transactional
public class NongBotController {

    @Transactional(readOnly = true) // select
    public NongBot getNongBotById(String id) {
        return nongBotMapper.selectNongBotById(id);
    }

    @Transactional(readOnly = true) // select
    public List<NongBot> getAllNongBot() {
        return nongBotMapper.selectAllNongBot();
    }

    @Transactional // Insert, Delete, Update
    public void registerNongBot(NongBot nongBot) {
        nongBotMapper.insertNongBot(nongBot);
    }

    protected NongBotController(NongBotMapper nongBotMapper) {
        super();
        this.nongBotMapper = nongBotMapper;
    }

    private final NongBotMapper nongBotMapper;
}
