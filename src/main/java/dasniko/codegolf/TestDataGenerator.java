package dasniko.codegolf;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
public class TestDataGenerator {

    private static final int CAP = 1000;

    Map<String, Integer> getTestData() {
        Map<String, Integer> testData = new HashMap<>(CAP);
        List<String> data = generateRandomDates();
        data.forEach(d -> {
            testData.put(d, getDayOfWeek(d));
        });
        return testData;
    }

    private List<String> generateRandomDates() {
        Random random = new Random();
        int minDay = (int) LocalDate.of(1583, 1, 1).toEpochDay();
        int maxDay = (int) LocalDate.of(2100, 12, 31).toEpochDay();

        List<String> data = new ArrayList<>(CAP);

        IntStream.range(0, CAP).forEach(i -> {
            long randomDay = minDay + random.nextInt(maxDay - minDay);
            LocalDate randomDate = LocalDate.ofEpochDay(randomDay);
            data.add(randomDate.format(DateTimeFormatter.ISO_DATE));
        });

        return data;
    }

    private Integer getDayOfWeek(String dt) {
        DayOfWeek dayOfWeek = LocalDate.parse(dt, DateTimeFormatter.ISO_DATE).getDayOfWeek();
        int v = dayOfWeek.getValue() + 1;
        if (v >= 7) {
            v = v - 7;
        }
        return v;
    }
}
