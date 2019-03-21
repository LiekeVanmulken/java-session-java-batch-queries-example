package entities;

import javax.persistence.*;

@Entity
//@Table(name = "records",schema="public")
public class Record {
    private Long id;
    private String content;

    public Record(String content) {
        this.content = content;
    }

    public Record() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
