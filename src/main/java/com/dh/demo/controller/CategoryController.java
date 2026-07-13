package com.dh.demo.controller;

import com.dh.demo.dto.CategoryDto;
import com.dh.demo.service.ICategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/category")
public class CategoryController {

    private final ICategoryService service;

    public CategoryController(ICategoryService service) {
        this.service = service;
    }

    @PostMapping("/save")
    public ResponseEntity<CategoryDto> save(@RequestBody CategoryDto categoryDto) {
        CategoryDto saved = service.save(categoryDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<CategoryDto> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public ResponseEntity<List<CategoryDto>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<CategoryDto> update(@PathVariable Integer id,
                                              @RequestBody CategoryDto categoryDto) {
        CategoryDto updated = service.update(id, categoryDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}