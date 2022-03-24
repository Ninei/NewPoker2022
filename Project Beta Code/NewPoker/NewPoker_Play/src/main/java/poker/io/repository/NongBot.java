package poker.io.repository;

import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.sql.Timestamp;

@Data
@Alias("NongBot")
public class NongBot { // DTO

    public NongBot(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public NongBot(String id, String name, Timestamp registerDate) {
        this(id, name);
        this.registerDate = registerDate;
    }

    private String id;
    private String name;
    private Timestamp registerDate;
}