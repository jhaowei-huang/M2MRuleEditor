package iot.ttu.edu.M2MRuleEditor.data;

/**
 * Created by user on 2014/11/5.
 */
public class ClassID extends Entity {
    private long id;
    private String name;


    public ClassID() {
    }

    public ClassID(long id, String name) {
        this.id = id;
        this.name = name ;
    }

    @Override
    public String toString() {
        return "Ip{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

