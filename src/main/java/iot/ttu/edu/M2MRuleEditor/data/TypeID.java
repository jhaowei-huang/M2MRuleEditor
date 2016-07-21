package iot.ttu.edu.M2MRuleEditor.data;

/**
 * Created by user on 2014/11/5.
 */
public class TypeID extends Entity {
    private long id;
    private long classID;
    private String name;


    public TypeID() {
    }

    public TypeID(long id,long classID , String name) {
        this.id = id;
        this.classID = classID;
        this.name = name ;
    }

    @Override
    public String toString() {
        return "Ip{" +
                "id=" + id +
                ", classID='" + classID + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public long getClassID() {
        return classID;
    }

    public void setClassID(long classID) {
        this.classID = classID;
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

