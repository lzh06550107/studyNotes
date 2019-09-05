package javaDemo;

public class Student1 {
    int no;
    String name;
    String sex;
    float height;

    public Student(int no, String name, String sex, float height) {
        this.no = no;
        this.name = name;
        this.sex = sex;
        this.height = height;
    }

    //****
}

Student stuA = new Student(1, "A", "M", 184);
Student stuB = new Student(2, "B", "G", 163);
Student stuC = new Student(3, "C", "M", 175);
Student stuD = new Student(4, "D", "G", 158);
Student stuE = new Student(5, "E", "M", 170);
List<Student> list = new ArrayList<>();
list.add(stuA);
list.add(stuB);
list.add(stuC);
list.add(stuD);
list.add(stuE);

Iterator<Student> iterator = list.iterator();
while(iterator.hasNext()) {
    Student stu = iterator.next();
    if (stu.getSex().equals("G")) {

        System.out.println(stu.toString());
    }
}

list.stream()
    .filter(student -> student.getSex().equals("G"))
    .forEach(student -> System.out.println(student.toString()));

long count = allArtists.stream()
    .filter(artist -> {
        System.out.println(artist.getName());
            return artist.isFrom("London");
        })
    .count();

Stream<Double> generateA = Stream.generate(new Supplier<Double>() {
    @Override
    public Double get() {
        return java.lang.Math.random();
    }
});

Stream<Double> generateB = Stream.generate(()-> java.lang.Math.random());
Stream<Double> generateC = Stream.generate(java.lang.Math::random);

Stream.iterate(1, item -> item + 1)
        .limit(10)
        .forEach(System.out::println); 
        // 打印结果：1，2，3，4，5，6，7，8，9，10

public interface Collection<E> extends Iterable<E> {
	***
	default Stream<E> stream() {
		return StreamSupport.stream(spliterator(), false);
	}
	***
}

public class Arrays {
    ***
    public static <T> Stream<T> stream(T[] array) {
        return stream(array, 0, array.length);
    }

   public static LongStream stream(long[] array) {
        return stream(array, 0, array.length);
    }
    ***
｝

int ids[] = new int[]{1, 2, 3, 4};
Arrays.stream(ids).forEach(System.out::println);

Stream.concat(Stream.of(1, 2, 3), Stream.of(4, 5))
       .forEach(integer -> System.out.print(integer + "  "));
// 打印结果
// 1  2  3  4  5  

Stream.of(1,2,3,1,2,3)
        .distinct()
        .forEach(System.out::println); // 打印结果：1，2，3

Stream.of(1, 2, 3, 4, 5)
        .filter(item -> item > 3)
        .forEach(System.out::println);// 打印结果：4，5

Stream.of("a", "b", "hello")
        .map(item-> item.toUpperCase())
        .forEach(System.out::println);
        // 打印结果
        // A, B, HELLO

Stream.of(1, 2, 3)
    .flatMap(integer -> Stream.of(integer * 10))
    .forEach(System.out::println);
    // 打印结果
    // 10，20，30

Stream.of(1, 2, 3, 4, 5)
        .peek(integer -> System.out.println("accept:" + integer))
        .forEach(System.out::println);
// 打印结果
// accept:1
//  1
//  accept:2
//  2
//  accept:3
//  3
//  accept:4
//  4
//  accept:5
//  5

Stream.of(1, 2, 3,4,5).skip(2) 
.forEach(System.out::println); 
// 打印结果 
// 3,4,5

Stream.of(5, 4, 3, 2, 1)
        .sorted()
        .forEach(System.out::println);
        // 打印结果
        // 1，2，3,4,5

Stream.of(1, 2, 3, 4, 5)
        .sorted(comparator) //这里是从大到小排序的比较器
        .forEach(System.out::println);
        // 打印结果
        // 5, 4, 3, 2, 1

long count = Stream.of(1, 2, 3, 4, 5).count();
System.out.println("count:" + count);// 打印结果：count:5

Stream.of(5, 4, 3, 2, 1).sorted().forEach(System.out::println);
    // 打印结果:1，2，3, 4, 5

Stream.of(5,2,1,4,3).forEachOrdered(integer -> {
            System.out.println("integer:"+integer);
        }); 
        // 打印结果
        // integer:5
        // integer:2
        // integer:1
        // integer:4
        // integer:3

Optional<Integer> max = Stream.of(1, 2, 3, 4, 5).max((o1, o2) -> o2 - o1);
System.out.println("max:" + max.get());// 打印结果：max:1

Optional<Integer> max = Stream.of(1, 2, 3, 4, 5).max((o1, o2) -> o1 - o2);
System.out.println("max:" + max.get());// 打印结果：min:5

boolean allMatch = Stream.of(1, 2, 3, 4).allMatch(integer -> integer > 0);
System.out.println("allMatch: " + allMatch); // 打印结果：allMatch: true 

boolean anyMatch = Stream.of(1, 2, 3, 4).anyMatch(integer -> integer > 3);
System.out.println("anyMatch: " + anyMatch); // 打印结果：anyMatch: true 

Optional<Integer> any = Stream.of(1, 2, 3, 4).findAny(); //1

Optional<Integer> any = Stream.of(1, 2, 3, 4).findFirst();//1

Stream.of(1, 2, 3,4,5).limit(2).forEach(System.out::println);
        // 打印结果:1,2

boolean noneMatch = Stream.of(1, 2, 3, 4, 5).noneMatch(integer -> integer > 10);
System.out.println("noneMatch:" + noneMatch); // 打印结果 noneMatch:true

boolean noneMatch_ = Stream.of(1, 2, 3, 4, 5).noneMatch(integer -> integer < 3);
System.out.println("noneMatch_:" + noneMatch_); // 打印结果 noneMatch_:false