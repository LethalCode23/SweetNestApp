package com.dh.demo.config;

import com.dh.demo.entity.Category;
import com.dh.demo.entity.City;
import com.dh.demo.entity.Department;
import com.dh.demo.entity.Pais;
import com.dh.demo.repository.CategoryRepository;
import com.dh.demo.repository.CityRepository;
import com.dh.demo.repository.DepartmentRepository;
import com.dh.demo.repository.PaisRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    private final PaisRepository paisRepository;
    private final DepartmentRepository departmentRepository;
    private final CityRepository cityRepository;
    private final CategoryRepository categoryRepository;

    public DataInitializer(PaisRepository paisRepository,
                           DepartmentRepository departmentRepository,
                           CityRepository cityRepository,
                           CategoryRepository categoryRepository
    ) {

        this.paisRepository = paisRepository;
        this.departmentRepository = departmentRepository;
        this.cityRepository = cityRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(ApplicationArguments args) {

        if (paisRepository.count() > 0) {
            return;
        }

        // ── Countries ────────────────────────────────────────────────────────────

        Pais colombia = new Pais();
        colombia.setPaiName("Colombia");
        colombia.setPaiState('A');
        colombia = paisRepository.save(colombia);

        Pais argentina = new Pais();
        argentina.setPaiName("Argentina");
        argentina.setPaiState('A');
        argentina = paisRepository.save(argentina);

        Pais mexico = new Pais();
        mexico.setPaiName("México");
        mexico.setPaiState('A');
        mexico = paisRepository.save(mexico);

        // ── Departments (Colombia) ──────────────────────────────────────────
        Department cundinamarca = CreateDepartment("Cundinamarca", colombia);
        Department antioquia    = CreateDepartment("Antioquia",    colombia);
        Department santander    = CreateDepartment("Santander",    colombia);
        Department valle        = CreateDepartment("Valle del Cauca", colombia);
        Department atlantico    = CreateDepartment("Atlántico",    colombia);

        // ── Departments (Argentina) ─────────────────────────────────────────
        Department buenosAires  = CreateDepartment("Buenos Aires", argentina);
        Department cordoba      = CreateDepartment("Córdoba",      argentina);

        // ── Departments (México) ────────────────────────────────────────────
        Department cdmx         = CreateDepartment("Ciudad de México", mexico);
        Department jalisco       = CreateDepartment("Jalisco",          mexico);

        // ── Cities (Colombia) ───────────────────────────────────────────────
        createCity("Bogotá",          cundinamarca);
        createCity("Soacha",          cundinamarca);
        createCity("Medellín",        antioquia);
        createCity("Bello",           antioquia);
        createCity("Bucaramanga",     santander);
        createCity("Floridablanca",   santander);
        createCity("Girón",           santander);
        createCity("Cali",            valle);
        createCity("Palmira",         valle);
        createCity("Barranquilla",    atlantico);
        createCity("Soledad",         atlantico);

        // ── Cities (Argentina) ──────────────────────────────────────────────
        createCity("Buenos Aires",    buenosAires);
        createCity("La Plata",        buenosAires);
        createCity("Córdoba Capital", cordoba);

        // ── Cities (México) ─────────────────────────────────────────────────
        createCity("Ciudad de México", cdmx);
        createCity("Guadalajara",      jalisco);
        createCity("Zapopan",          jalisco);

        // - Categories ─────────────────────────────────────────────────
        createCategory("Playa", 'A');
        createCategory("Boutique", 'A');
        createCategory("Económico", 'A');
    }

    private Department CreateDepartment(String name, Pais country) {

        Department dep = new Department();
        dep.setDepName(name);
        dep.setPais(country);
        dep.setDepState('A');

        return departmentRepository.save(dep);
    }

    private void createCity(String name, Department department) {

        City city = new City();

        city.setCitName(name);
        city.setDepartment(department);
        city.setCitState('A');
        cityRepository.save(city);
    }

    private void createCategory(String catName, Character catSte) {

        Category category = new Category();

        category.setCatName(catName);
        category.setCatEst(catSte);

        categoryRepository.save(category);
    }
}