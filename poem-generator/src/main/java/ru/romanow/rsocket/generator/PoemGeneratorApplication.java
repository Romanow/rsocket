package ru.romanow.rsocket.generator;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import ru.romanow.rsocket.generator.model.Column;
import ru.romanow.rsocket.generator.model.ErrorResponse;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.nio.file.Files.newBufferedReader;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.slf4j.LoggerFactory.getLogger;

@SpringBootApplication
public class PoemGeneratorApplication {
    public static void main(String[] args) {
        SpringApplication.run(PoemGeneratorApplication.class, args);
    }
}

@Controller
class MessageController {
    private static final Logger logger = getLogger(MessageController.class);
    private static final String WORDS_CSV = "data/words.csv";

    private List<Column> words;
    private int columnSize;

    @SneakyThrows
    @PostConstruct
    public void init() {
        // https://github.com/rsocket/rsocket-java/issues/1018
        Hooks.onErrorDropped(error -> {});

        try (final BufferedReader reader = newBufferedReader(new ClassPathResource(WORDS_CSV).getFile().toPath());
             final CSVReader csvReader = new CSVReaderBuilder(reader)
                     .withSkipLines(1)
                     .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                     .build()) {

            String[] line;
            final List<List<String>> data = new ArrayList<>();
            while ((line = csvReader.readNext()) != null) {
                data.add(Arrays.asList(line));
            }

            int n = data.stream().mapToInt(List::size).max().getAsInt();
            final List<Iterator<String>> iterators = data.stream().map(List::iterator).collect(toList());
            this.words = IntStream.range(0, n)
                    .mapToObj(i -> iterators.stream()
                            .filter(Iterator::hasNext)
                            .map(Iterator::next)
                            .collect(Collectors.toCollection(Column::new)))
                    .collect(toList());
            this.columnSize = data.size();
        }
    }

    @MessageMapping("generate")
    public Mono<String> generate() {
        return Mono.fromSupplier(() -> words.stream()
                .map(column -> column.get(nextInt(0, this.columnSize)))
                .collect(joining(" ")))
                .doOnSuccess(logger::info);
    }

    @MessageExceptionHandler
    public Mono<ErrorResponse> handleException(Exception exception) {
        return Mono.just(new ErrorResponse(exception.getMessage()));
    }
}
