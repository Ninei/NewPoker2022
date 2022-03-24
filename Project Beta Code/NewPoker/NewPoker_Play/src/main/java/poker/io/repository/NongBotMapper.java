package poker.io.repository;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface NongBotMapper { // DAO
    NongBot selectNongBotById(String id);
    List<NongBot> selectAllNongBot();
    void insertNongBot(NongBot nongBot);
}
