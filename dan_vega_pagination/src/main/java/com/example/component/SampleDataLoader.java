package com.example.component;

import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import com.example.entity.Address;
import com.example.entity.Person;
import com.example.repository.PersonRepository;

import java.util.List;
import java.util.stream.IntStream;

@Component
public class SampleDataLoader implements CommandLineRunner {

    private final PersonRepository repository;
    private final Faker faker;

    public SampleDataLoader(PersonRepository repository) {
        this.repository = repository;
        this.faker = new Faker();
    }

    @Override
    public void run(String... args) throws Exception {

        // create 100 rows of people in the database
        List<Person> people = IntStream.rangeClosed(1, 100)
                .mapToObj(i -> new Person(
                        faker.name().firstName(),
                        faker.name().lastName(),
                        faker.phoneNumber().cellPhone(),
                        faker.internet().emailAddress(),
                        new Address(
                                faker.address().streetAddress(),
                                faker.address().city(),
                                faker.address().state(),
                                faker.address().zipCode()
                        )
                )).toList();

        repository.saveAll(people); // Use the default saveAll method to save data
    }
}
