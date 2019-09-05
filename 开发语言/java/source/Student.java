package javaDemo;

public class Student {
    private String name;
    private Integer age;
    private String className;
    
    public Student(String name,Integer age, String className){
        this.name=name;
        this.age=age;
        this.className = className;
    }
    public String getName() {
        return name;
    }
    public Integer getAge() {
        return age;
    }
    public String getClassName() {
        return className;
    }
}  
