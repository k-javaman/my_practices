package com.example.repository;

import com.example.entity.Person;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface PersonRepository extends PagingAndSortingRepository<Person, Integer> {

}
