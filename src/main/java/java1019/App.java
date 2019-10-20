package java1019;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * Hello world!
 */
@SpringBootApplication
@RestController
public class App implements CommandLineRunner {
    @GetMapping("test")
    public String web(String param) {
        int sum = Stream
                .generate(String::new)
                .map(a -> a + "test")
                .limit(100)
                .mapToInt(String::length)
                .sum();
        return """
                    ||||
            ||| =hello world= |||
""" + sum;
    }

    public static void main(String[] args) {
        System.out.println("""
                   ||||
            ||| =hello world= |||
      """);
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Executors.newScheduledThreadPool(1)
                .scheduleAtFixedRate(() -> {
                    try {
                        var request = HttpRequest.newBuilder()
                                .uri(URI.create("http://localhost:8080/test"))
                                .GET().build();
                        var client = HttpClient.newHttpClient();
                        HttpResponse<String> response = client
                                .send(request,
                                        HttpResponse.BodyHandlers.ofString());
                        System.out.println(response.body());
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.exit(-1);
                    }

                }, 1, 3, TimeUnit.SECONDS);
    }
}
