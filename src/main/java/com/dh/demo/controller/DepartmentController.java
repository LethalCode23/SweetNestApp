package com.dh.demo.controller;

import com.dh.demo.dto.DepartmentDto;
import com.dh.demo.service.IDepartmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/department")
public class DepartmentController {

    private final IDepartmentService service;

    public DepartmentController(IDepartmentService service) {
        this.service = service;
    }

    @PostMapping("/save")
    public ResponseEntity<DepartmentDto> save(@RequestBody DepartmentDto departmentDto) {

        DepartmentDto saved = service.save(departmentDto);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/all")
    public ResponseEntity<List<DepartmentDto>> getDepartments() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<DepartmentDto> findById(@PathVariable Integer id) {

        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<DepartmentDto> update(@PathVariable Integer id,
                                                @RequestBody DepartmentDto departmentDto) {

        DepartmentDto updated = service.update(id, departmentDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {

        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}