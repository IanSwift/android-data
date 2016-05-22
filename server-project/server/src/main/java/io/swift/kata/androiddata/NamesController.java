package io.swift.kata.androiddata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.bind.annotation.*;

@RestController
public class NamesController {

    private NamesRepository repository;

    @Autowired
    public NamesController(NamesRepository repository) {
        this.repository = repository;
    }

    @RequestMapping(path = "/names", method = RequestMethod.POST)
    public boolean postName(@RequestBody Name name) {
        repository.save(name);
        return true;
    }

    @RequestMapping(path = "/names/{id}", method = RequestMethod.DELETE)
    public boolean deleteName(@PathVariable Long id) {
        try {
            repository.delete(id);
        }
        catch (EmptyResultDataAccessException e) {
        }
        return true;
    }
}
