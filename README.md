
* * *
# 所有版本历史完整更新说明
#### oracle官方文档翻译删去无用信息
## 更新说明
- [jdk13更新说明](https://app.yinxiang.com/fx/d8a36d24-6c6a-475c-ab1d-4e34017d443a)
- [jdk12更新说明](https://app.yinxiang.com/fx/d06c2210-4acc-4ef8-afab-0d559448af2e)
- [jdk11更新说明](https://app.yinxiang.com/fx/efb4148f-7fa6-4156-81ff-11057d482d9d)
- [jdk10更新说明](https://app.yinxiang.com/fx/1a2c5b5e-8f96-4729-b436-31c07d4926b4)
- [jdk9更新说明](https://app.yinxiang.com/fx/73dd2f92-35ce-4f7f-b06f-c983dbd9dfab)
- [jdk8更新说明](https://app.yinxiang.com/fx/fd1511bc-a373-44a2-810b-06338dc7ce76)
- [jdk7更新说明](https://app.yinxiang.com/fx/1f45a265-8d80-419b-8a50-0efa85adb81b)
## 迁移指南
- [jdk13迁移指南](https://app.yinxiang.com/fx/7ac8ba2b-41de-4e92-898d-2295361781e7)
- [jdk12迁移指南](https://app.yinxiang.com/fx/2e60a149-c6e2-4246-a3e9-900bccb47938)
- [jdk11迁移指南](https://app.yinxiang.com/fx/d67e99f6-2f15-4f8d-afbc-26b443e4bf18)
- [jdk10迁移指南](https://app.yinxiang.com/fx/8b7ac790-a89e-488a-b8e3-1b5bb392fa2d)
- [jdk9迁移指南](https://app.yinxiang.com/fx/52636ff9-842b-40e2-a608-1d942014957e)
- [jdk8迁移指南](https://app.yinxiang.com/fx/fd1511bc-a373-44a2-810b-06338dc7ce76)
- [jdk7迁移指南](https://app.yinxiang.com/fx/3e0bb39c-6441-4afd-9431-496515422424)

* * *
# JAVA7-JAVA8重要变化

## 默认方法

default关键字，定义接口不需要实现默认方法，当然也可以像一般的方法那样覆盖

```java
interface Formula {
    double calculate(int a);

    default double sqrt(int a) {
        return Math.sqrt(a);
    }
}
```

## Lambda 表达式

Lambda的值你可以当做一个对象，这个对象实现一个接口。而接口必须只有一个默认方法。
java:7：
```java
List<String> names = Arrays.asList("peter", "anna", "mike", "xenia");

Collections.sort(names, new Comparator<String>() {
    @Override
    public int compare(String a, String b) {
        return b.compareTo(a);
    }
});
```

java8

```java
Collections.sort(names, (String a, String b) -> {
    return b.compareTo(a);
});
```


甚至
```java
Collections.sort(names, (String a, String b) -> b.compareTo(a));
```

甚至

```java
names.sort((a, b) -> b.compareTo(a));
```


## Functional Interfaces函数接口

lambda表达式如何适应Java的类型系统?每个lambda都对应于由接口指定的给定类型。所谓的函数接口必须包含一个抽象方法声明。该类型的每个lambda表达式都将被匹配到这个抽象方法。由于默认方法不是抽象的，您可以自由地将默认方法添加到函数接口。

只要接口只包含一个抽象方法，我们就可以使用任意接口作为lambda表达式。为了确保您的接口满足需求，您应该添加@FunctionalInterface注释。编译器知道这个注释并在您试图向接口添加第二个抽象方法声明时抛出编译器错误。

```java
@FunctionalInterface
interface Converter<F, T> {
    T convert(F from);
}
```

```java
Converter<String, Integer> converter = (from) -> Integer.valueOf(from);
Integer converted = converter.convert("123");
System.out.println(converted);    // 123
```

请记住，如果忽略' @FunctionalInterface '注释，代码也是有效的。


## Method and Constructor References方法引用

The above example code can be further simplified by utilizing static method references:

```java
Converter<String, Integer> converter = Integer::valueOf;
Integer converted = converter.convert("123");
System.out.println(converted);   // 123
```

Java 8 enables you to pass references of methods or constructors via the `::` keyword. The above example shows how to reference a static method. But we can also reference object methods:

```java
class Something {
    String startsWith(String s) {
        return String.valueOf(s.charAt(0));
    }
}
```

```java
Something something = new Something();
Converter<String, String> converter = something::startsWith;
String converted = converter.convert("Java");
System.out.println(converted);    // "J"
```

Let's see how the `::` keyword works for constructors. First we define an example class with different constructors:

```java
class Person {
    String firstName;
    String lastName;

    Person() {}

    Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
```

Next we specify a person factory interface to be used for creating new persons:

```java
interface PersonFactory<P extends Person> {
    P create(String firstName, String lastName);
}
```

Instead of implementing the factory manually, we glue everything together via constructor references:

```java
PersonFactory<Person> personFactory = Person::new;
Person person = personFactory.create("Peter", "Parker");
```

We create a reference to the Person constructor via `Person::new`. The Java compiler automatically chooses the right constructor by matching the signature of `PersonFactory.create`.

## Lambda Scopes

Accessing outer scope variables from lambda expressions is very similar to anonymous objects. You can access final variables from the local outer scope as well as instance fields and static variables.

### Accessing local variables

We can read final local variables from the outer scope of lambda expressions:

```java
final int num = 1;
Converter<Integer, String> stringConverter =
        (from) -> String.valueOf(from + num);

stringConverter.convert(2);     // 3
```

But different to anonymous objects the variable `num` does not have to be declared final. This code is also valid:

```java
int num = 1;
Converter<Integer, String> stringConverter =
        (from) -> String.valueOf(from + num);

stringConverter.convert(2);     // 3
```

However `num` must be implicitly final for the code to compile. The following code does **not** compile:

```java
int num = 1;
Converter<Integer, String> stringConverter =
        (from) -> String.valueOf(from + num);
num = 3;
```

Writing to `num` from within the lambda expression is also prohibited.

### Accessing fields and static variables

In contrast to local variables, we have both read and write access to instance fields and static variables from within lambda expressions. This behaviour is well known from anonymous objects.

```java
class Lambda4 {
    static int outerStaticNum;
    int outerNum;

    void testScopes() {
        Converter<Integer, String> stringConverter1 = (from) -> {
            outerNum = 23;
            return String.valueOf(from);
        };

        Converter<Integer, String> stringConverter2 = (from) -> {
            outerStaticNum = 72;
            return String.valueOf(from);
        };
    }
}
```

### Accessing Default Interface Methods

Remember the formula example from the first section? Interface `Formula` defines a default method `sqrt` which can be accessed from each formula instance including anonymous objects. This does not work with lambda expressions.

Default methods **cannot** be accessed from within lambda expressions. The following code does not compile:

```java
Formula formula = (a) -> sqrt(a * 100);
```


## Built-in Functional Interfaces

The JDK 1.8 API contains many built-in functional interfaces. Some of them are well known from older versions of Java like `Comparator` or `Runnable`. Those existing interfaces are extended to enable Lambda support via the `@FunctionalInterface` annotation.

But the Java 8 API is also full of new functional interfaces to make your life easier. Some of those new interfaces are well known from the [Google Guava](https://code.google.com/p/guava-libraries/) library. Even if you're familiar with this library you should keep a close eye on how those interfaces are extended by some useful method extensions.

### Predicates

Predicates are boolean-valued functions of one argument. The interface contains various default methods for composing predicates to complex logical terms (and, or, negate)

```java
Predicate<String> predicate = (s) -> s.length() > 0;

predicate.test("foo");              // true
predicate.negate().test("foo");     // false

Predicate<Boolean> nonNull = Objects::nonNull;
Predicate<Boolean> isNull = Objects::isNull;

Predicate<String> isEmpty = String::isEmpty;
Predicate<String> isNotEmpty = isEmpty.negate();
```

### Functions

Functions accept one argument and produce a result. Default methods can be used to chain multiple functions together (compose, andThen).

```java
Function<String, Integer> toInteger = Integer::valueOf;
Function<String, String> backToString = toInteger.andThen(String::valueOf);

backToString.apply("123");     // "123"
```

### Suppliers

Suppliers produce a result of a given generic type. Unlike Functions, Suppliers don't accept arguments.

```java
Supplier<Person> personSupplier = Person::new;
personSupplier.get();   // new Person
```

### Consumers

Consumers represent operations to be performed on a single input argument.

```java
Consumer<Person> greeter = (p) -> System.out.println("Hello, " + p.firstName);
greeter.accept(new Person("Luke", "Skywalker"));
```

### Comparators

Comparators are well known from older versions of Java. Java 8 adds various default methods to the interface.

```java
Comparator<Person> comparator = (p1, p2) -> p1.firstName.compareTo(p2.firstName);

Person p1 = new Person("John", "Doe");
Person p2 = new Person("Alice", "Wonderland");

comparator.compare(p1, p2);             // > 0
comparator.reversed().compare(p1, p2);  // < 0
```

## Optionals

Optionals are not functional interfaces, but nifty utilities to prevent `NullPointerException`. It's an important concept for the next section, so let's have a quick look at how Optionals work.

Optional is a simple container for a value which may be null or non-null. Think of a method which may return a non-null result but sometimes return nothing. Instead of returning `null` you return an `Optional` in Java 8.

```java
Optional<String> optional = Optional.of("bam");

optional.isPresent();           // true
optional.get();                 // "bam"
optional.orElse("fallback");    // "bam"

optional.ifPresent((s) -> System.out.println(s.charAt(0)));     // "b"
```

## Streams

A `java.util.Stream` represents a sequence of elements on which one or more operations can be performed. Stream operations are either _intermediate_ or _terminal_. While terminal operations return a result of a certain type, intermediate operations return the stream itself so you can chain multiple method calls in a row. Streams are created on a source, e.g. a `java.util.Collection` like lists or sets (maps are not supported). Stream operations can either be executed sequentially or parallely.

> Streams are extremely powerful, so I wrote a separate [Java 8 Streams Tutorial](http://winterbe.com/posts/2014/07/31/java8-stream-tutorial-examples/). **You should also check out [Sequency](https://github.com/winterbe/sequency) as a similiar library for the web.**

Let's first look how sequential streams work. First we create a sample source in form of a list of strings:

```java
List<String> stringCollection = new ArrayList<>();
stringCollection.add("ddd2");
stringCollection.add("aaa2");
stringCollection.add("bbb1");
stringCollection.add("aaa1");
stringCollection.add("bbb3");
stringCollection.add("ccc");
stringCollection.add("bbb2");
stringCollection.add("ddd1");
```

Collections in Java 8 are extended so you can simply create streams either by calling `Collection.stream()` or `Collection.parallelStream()`. The following sections explain the most common stream operations.

### Filter

Filter accepts a predicate to filter all elements of the stream. This operation is _intermediate_ which enables us to call another stream operation (`forEach`) on the result. ForEach accepts a consumer to be executed for each element in the filtered stream. ForEach is a terminal operation. It's `void`, so we cannot call another stream operation.

```java
stringCollection
    .stream()
    .filter((s) -> s.startsWith("a"))
    .forEach(System.out::println);

// "aaa2", "aaa1"
```

### Sorted

Sorted is an _intermediate_ operation which returns a sorted view of the stream. The elements are sorted in natural order unless you pass a custom `Comparator`.

```java
stringCollection
    .stream()
    .sorted()
    .filter((s) -> s.startsWith("a"))
    .forEach(System.out::println);

// "aaa1", "aaa2"
```

Keep in mind that `sorted` does only create a sorted view of the stream without manipulating the ordering of the backed collection. The ordering of `stringCollection` is untouched:

```java
System.out.println(stringCollection);
// ddd2, aaa2, bbb1, aaa1, bbb3, ccc, bbb2, ddd1
```

### Map

The _intermediate_ operation `map` converts each element into another object via the given function. The following example converts each string into an upper-cased string. But you can also use `map` to transform each object into another type. The generic type of the resulting stream depends on the generic type of the function you pass to `map`.

```java
stringCollection
    .stream()
    .map(String::toUpperCase)
    .sorted((a, b) -> b.compareTo(a))
    .forEach(System.out::println);

// "DDD2", "DDD1", "CCC", "BBB3", "BBB2", "AAA2", "AAA1"
```

### Match

Various matching operations can be used to check whether a certain predicate matches the stream. All of those operations are _terminal_ and return a boolean result.

```java
boolean anyStartsWithA =
    stringCollection
        .stream()
        .anyMatch((s) -> s.startsWith("a"));

System.out.println(anyStartsWithA);      // true

boolean allStartsWithA =
    stringCollection
        .stream()
        .allMatch((s) -> s.startsWith("a"));

System.out.println(allStartsWithA);      // false

boolean noneStartsWithZ =
    stringCollection
        .stream()
        .noneMatch((s) -> s.startsWith("z"));

System.out.println(noneStartsWithZ);      // true
```

#### Count

Count is a _terminal_ operation returning the number of elements in the stream as a `long`.

```java
long startsWithB =
    stringCollection
        .stream()
        .filter((s) -> s.startsWith("b"))
        .count();

System.out.println(startsWithB);    // 3
```


### Reduce

This _terminal_ operation performs a reduction on the elements of the stream with the given function. The result is an `Optional` holding the reduced value.

```java
Optional<String> reduced =
    stringCollection
        .stream()
        .sorted()
        .reduce((s1, s2) -> s1 + "#" + s2);

reduced.ifPresent(System.out::println);
// "aaa1#aaa2#bbb1#bbb2#bbb3#ccc#ddd1#ddd2"
```

## Parallel Streams

As mentioned above streams can be either sequential or parallel. Operations on sequential streams are performed on a single thread while operations on parallel streams are performed concurrently on multiple threads.

The following example demonstrates how easy it is to increase the performance by using parallel streams.

First we create a large list of unique elements:

```java
int max = 1000000;
List<String> values = new ArrayList<>(max);
for (int i = 0; i < max; i++) {
    UUID uuid = UUID.randomUUID();
    values.add(uuid.toString());
}
```

Now we measure the time it takes to sort a stream of this collection.

### Sequential Sort

```java
long t0 = System.nanoTime();

long count = values.stream().sorted().count();
System.out.println(count);

long t1 = System.nanoTime();

long millis = TimeUnit.NANOSECONDS.toMillis(t1 - t0);
System.out.println(String.format("sequential sort took: %d ms", millis));

// sequential sort took: 899 ms
```

### Parallel Sort

```java
long t0 = System.nanoTime();

long count = values.parallelStream().sorted().count();
System.out.println(count);

long t1 = System.nanoTime();

long millis = TimeUnit.NANOSECONDS.toMillis(t1 - t0);
System.out.println(String.format("parallel sort took: %d ms", millis));

// parallel sort took: 472 ms
```

As you can see both code snippets are almost identical but the parallel sort is roughly 50% faster. All you have to do is change `stream()` to `parallelStream()`.

## Maps

As already mentioned maps do not directly support streams. There's no `stream()` method available on the `Map` interface itself, however you can create specialized streams upon the keys, values or entries of a map via `map.keySet().stream()`, `map.values().stream()` and `map.entrySet().stream()`. 

Furthermore maps support various new and useful methods for doing common tasks.

```java
Map<Integer, String> map = new HashMap<>();

for (int i = 0; i < 10; i++) {
    map.putIfAbsent(i, "val" + i);
}

map.forEach((id, val) -> System.out.println(val));
```

The above code should be self-explaining: `putIfAbsent` prevents us from writing additional if null checks; `forEach` accepts a consumer to perform operations for each value of the map.

This example shows how to compute code on the map by utilizing functions:

```java
map.computeIfPresent(3, (num, val) -> val + num);
map.get(3);             // val33

map.computeIfPresent(9, (num, val) -> null);
map.containsKey(9);     // false

map.computeIfAbsent(23, num -> "val" + num);
map.containsKey(23);    // true

map.computeIfAbsent(3, num -> "bam");
map.get(3);             // val33
```

Next, we learn how to remove entries for a given key, only if it's currently mapped to a given value:

```java
map.remove(3, "val3");
map.get(3);             // val33

map.remove(3, "val33");
map.get(3);             // null
```

Another helpful method:

```java
map.getOrDefault(42, "not found");  // not found
```

Merging entries of a map is quite easy:

```java
map.merge(9, "val9", (value, newValue) -> value.concat(newValue));
map.get(9);             // val9

map.merge(9, "concat", (value, newValue) -> value.concat(newValue));
map.get(9);             // val9concat
```

Merge either put the key/value into the map if no entry for the key exists, or the merging function will be called to change the existing value.


## Date API

Java 8 contains a brand new date and time API under the package `java.time`. The new Date API is comparable with the [Joda-Time](http://www.joda.org/joda-time/) library, however it's [not the same](http://blog.joda.org/2009/11/why-jsr-310-isn-joda-time_4941.html). The following examples cover the most important parts of this new API.

### Clock

Clock provides access to the current date and time. Clocks are aware of a timezone and may be used instead of `System.currentTimeMillis()` to retrieve the current time in milliseconds since Unix EPOCH. Such an instantaneous point on the time-line is also represented by the class `Instant`. Instants can be used to create legacy `java.util.Date` objects.

```java
Clock clock = Clock.systemDefaultZone();
long millis = clock.millis();

Instant instant = clock.instant();
Date legacyDate = Date.from(instant);   // legacy java.util.Date
```

### Timezones

Timezones are represented by a `ZoneId`. They can easily be accessed via static factory methods. Timezones define the offsets which are important to convert between instants and local dates and times.

```java
System.out.println(ZoneId.getAvailableZoneIds());
// prints all available timezone ids

ZoneId zone1 = ZoneId.of("Europe/Berlin");
ZoneId zone2 = ZoneId.of("Brazil/East");
System.out.println(zone1.getRules());
System.out.println(zone2.getRules());

// ZoneRules[currentStandardOffset=+01:00]
// ZoneRules[currentStandardOffset=-03:00]
```

### LocalTime

LocalTime represents a time without a timezone, e.g. 10pm or 17:30:15. The following example creates two local times for the timezones defined above. Then we compare both times and calculate the difference in hours and minutes between both times.

```java
LocalTime now1 = LocalTime.now(zone1);
LocalTime now2 = LocalTime.now(zone2);

System.out.println(now1.isBefore(now2));  // false

long hoursBetween = ChronoUnit.HOURS.between(now1, now2);
long minutesBetween = ChronoUnit.MINUTES.between(now1, now2);

System.out.println(hoursBetween);       // -3
System.out.println(minutesBetween);     // -239
```

LocalTime comes with various factory methods to simplify the creation of new instances, including parsing of time strings.

```java
LocalTime late = LocalTime.of(23, 59, 59);
System.out.println(late);       // 23:59:59

DateTimeFormatter germanFormatter =
    DateTimeFormatter
        .ofLocalizedTime(FormatStyle.SHORT)
        .withLocale(Locale.GERMAN);

LocalTime leetTime = LocalTime.parse("13:37", germanFormatter);
System.out.println(leetTime);   // 13:37
```

### LocalDate

LocalDate represents a distinct date, e.g. 2014-03-11. It's immutable and works exactly analog to LocalTime. The sample demonstrates how to calculate new dates by adding or subtracting days, months or years. Keep in mind that each manipulation returns a new instance.

```java
LocalDate today = LocalDate.now();
LocalDate tomorrow = today.plus(1, ChronoUnit.DAYS);
LocalDate yesterday = tomorrow.minusDays(2);

LocalDate independenceDay = LocalDate.of(2014, Month.JULY, 4);
DayOfWeek dayOfWeek = independenceDay.getDayOfWeek();
System.out.println(dayOfWeek);    // FRIDAY
```

Parsing a LocalDate from a string is just as simple as parsing a LocalTime:

```java
DateTimeFormatter germanFormatter =
    DateTimeFormatter
        .ofLocalizedDate(FormatStyle.MEDIUM)
        .withLocale(Locale.GERMAN);

LocalDate xmas = LocalDate.parse("24.12.2014", germanFormatter);
System.out.println(xmas);   // 2014-12-24
```

### LocalDateTime

LocalDateTime represents a date-time. It combines date and time as seen in the above sections into one instance. `LocalDateTime` is immutable and works similar to LocalTime and LocalDate. We can utilize methods for retrieving certain fields from a date-time:

```java
LocalDateTime sylvester = LocalDateTime.of(2014, Month.DECEMBER, 31, 23, 59, 59);

DayOfWeek dayOfWeek = sylvester.getDayOfWeek();
System.out.println(dayOfWeek);      // WEDNESDAY

Month month = sylvester.getMonth();
System.out.println(month);          // DECEMBER

long minuteOfDay = sylvester.getLong(ChronoField.MINUTE_OF_DAY);
System.out.println(minuteOfDay);    // 1439
```

With the additional information of a timezone it can be converted to an instant. Instants can easily be converted to legacy dates of type `java.util.Date`.

```java
Instant instant = sylvester
        .atZone(ZoneId.systemDefault())
        .toInstant();

Date legacyDate = Date.from(instant);
System.out.println(legacyDate);     // Wed Dec 31 23:59:59 CET 2014
```

Formatting date-times works just like formatting dates or times. Instead of using pre-defined formats we can create formatters from custom patterns.

```java
DateTimeFormatter formatter =
    DateTimeFormatter
        .ofPattern("MMM dd, yyyy - HH:mm");

LocalDateTime parsed = LocalDateTime.parse("Nov 03, 2014 - 07:13", formatter);
String string = formatter.format(parsed);
System.out.println(string);     // Nov 03, 2014 - 07:13
```

Unlike `java.text.NumberFormat` the new `DateTimeFormatter` is immutable and **thread-safe**.

For details on the pattern syntax read [here](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html).


## Annotations

Annotations in Java 8 are repeatable. Let's dive directly into an example to figure that out.

First, we define a wrapper annotation which holds an array of the actual annotations:

```java
@interface Hints {
    Hint[] value();
}

@Repeatable(Hints.class)
@interface Hint {
    String value();
}
```
Java 8 enables us to use multiple annotations of the same type by declaring the annotation `@Repeatable`.

### Variant 1: Using the container annotation (old school)

```java
@Hints({@Hint("hint1"), @Hint("hint2")})
class Person {}
```

### Variant 2: Using repeatable annotations (new school)

```java
@Hint("hint1")
@Hint("hint2")
class Person {}
```

Using variant 2 the java compiler implicitly sets up the `@Hints` annotation under the hood. That's important for reading annotation information via reflection.

```java
Hint hint = Person.class.getAnnotation(Hint.class);
System.out.println(hint);                   // null

Hints hints1 = Person.class.getAnnotation(Hints.class);
System.out.println(hints1.value().length);  // 2

Hint[] hints2 = Person.class.getAnnotationsByType(Hint.class);
System.out.println(hints2.length);          // 2
```

Although we never declared the `@Hints` annotation on the `Person` class, it's still readable via `getAnnotation(Hints.class)`. However, the more convenient method is `getAnnotationsByType` which grants direct access to all annotated `@Hint` annotations.


Furthermore the usage of annotations in Java 8 is expanded to two new targets:

```java
@Target({ElementType.TYPE_PARAMETER, ElementType.TYPE_USE})
@interface MyAnnotation {}
```
## java9-java11

* * *


```java
String text = "Hello Java 9";

var text = "Hello Java 10";

var text = "Hello Java 11";
text = 23;  // Incompatible types

final var text = "Banana";
text = "Joe";   // Cannot assign a value to final variable 'text'

// Cannot infer type:var a;var nothing = null;var lambda = () -> System.out.println("Pity!");var method = this::someMethod;

var myList = new ArrayList<Map<String, List<Integer>>>();

for (var current : myList) {
    // current is infered to type: Map<String, List<Integer>>
    System.out.println(current);}
    
Predicate<String> predicate = (@Nullable var a) -> true;
```


```java

var request = HttpRequest.newBuilder()
    .uri(URI.create("https://winterbe.com"))
    .GET()
    .build();var client = HttpClient.newHttpClient();
HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
System.out.println(response.body());


var request = HttpRequest.newBuilder()
    .uri(URI.create("https://winterbe.com"))
    .build();var client = HttpClient.newHttpClient();
client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
    .thenApply(HttpResponse::body)
    .thenAccept(System.out::println);
    
    
var request = HttpRequest.newBuilder()
    .uri(URI.create("https://postman-echo.com/post"))
    .header("Content-Type", "text/plain")
    .POST(HttpRequest.BodyPublishers.ofString("Hi there!"))
    .build();var client = HttpClient.newHttpClient();var response = client.send(request, HttpResponse.BodyHandlers.ofString());
System.out.println(response.statusCode());      // 200


var request = HttpRequest.newBuilder()
    .uri(URI.create("https://postman-echo.com/basic-auth"))
    .build();var client = HttpClient.newBuilder()
    .authenticator(new Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication("postman", "password".toCharArray());
        }
    })
    .build();var response = client.send(request, HttpResponse.BodyHandlers.ofString());
System.out.println(response.statusCode());      // 200

```

集合

```java

var list = List.of("A", "B", "C");var copy = List.copyOf(list);
System.out.println(list == copy);   // true

var map = Map.of("A", 1, "B", 2);
System.out.println(map);    // {B=2, A=1}
```

流

```java

Stream.ofNullable(null)
    .count()   // 0
    
Stream.of(1, 2, 3, 2, 1)
    .dropWhile(n -> n < 3)
    .collect(Collectors.toList());  // [3, 2, 1]

Stream.of(1, 2, 3, 2, 1)
    .takeWhile(n -> n < 3)
    .collect(Collectors.toList());  // [1, 2]
    
Optional.of("foo").orElseThrow();     // foo
Optional.of("foo").stream().count();  // 1
Optional.ofNullable(null)
    .or(() -> Optional.of("fallback"))
    .get();     
```


One of the most basic classes String gets a few helper methods for trimming or checking whitespace and for streaming the lines of a string:
```java

var classLoader = ClassLoader.getSystemClassLoader();var inputStream = classLoader.getResourceAsStream("myFile.txt");var tempFile = File.createTempFile("myFileCopy", "txt");try (var outputStream = new FileOutputStream(tempFile)) {
    inputStream.transferTo(outputStream);}
```
## java12-13
```java
public static void main( String[] args )
{  
var a="111";   
var s=   switch (a){ 
case "1"+2->11;     
case "11"+1->22;      
default  -> "1";    
}; 
System.out.println(s); 
String sql=""" 
select * from table 
where name='sasasas' 
"""; 
System.out.println(sql.trim());
}

```
