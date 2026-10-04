package com.dh.demo.config;

import com.dh.demo.entity.*;
import com.dh.demo.entity.Module;
import com.dh.demo.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements ApplicationRunner {

    private static final List<Character> FULL_CRUD = List.of('R', 'C', 'U', 'D');

    private record SubModuleSpec(String name, String url) {}
    private final PaisRepository paisRepository;
    private final DepartmentRepository departmentRepository;
    private final CityRepository cityRepository;
    private final CategoryRepository categoryRepository;
    private final HotelRepository hotelRepository;
    private final IFeatureRepository featureRepository;
    private final IProfileRepository IProfileRepository;
    private final IModuleRepository moduleRepository;
    private final ISubModuleRepository subModuleRepository;
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final IPermissionProfilesRepository permissionProfilesRepository;
    private final IPermissionProfilesModuleRepository permissionProfilesModuleRepository;
    private final IPermissionProfilesModulePerRepository permissionProfilesModulePerRepository;

    public DataInitializer(PaisRepository paisRepository,
                           DepartmentRepository departmentRepository,
                           CityRepository cityRepository,
                           CategoryRepository categoryRepository,
                           HotelRepository hotelRepository,
                           IProfileRepository IProfileRepository,
                           IModuleRepository moduleRepository,
                           ISubModuleRepository subModuleRepository,
                           IUserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           IPermissionProfilesRepository permissionProfilesRepository,
                           IPermissionProfilesModuleRepository permissionProfilesModuleRepository,
                           IPermissionProfilesModulePerRepository permissionProfilesModulePerRepository,
                           IFeatureRepository featureRepository
    ) {
        this.paisRepository = paisRepository;
        this.departmentRepository = departmentRepository;
        this.cityRepository = cityRepository;
        this.categoryRepository = categoryRepository;
        this.hotelRepository = hotelRepository;
        this.IProfileRepository = IProfileRepository;
        this.moduleRepository = moduleRepository;
        this.subModuleRepository = subModuleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.permissionProfilesRepository = permissionProfilesRepository;
        this.permissionProfilesModuleRepository = permissionProfilesModuleRepository;
        this.permissionProfilesModulePerRepository = permissionProfilesModulePerRepository;
        this.featureRepository = featureRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        if (paisRepository.count() > 0) {
            return;
        }

        // ── Profiles ─────────────────────────────────────────────────────────
        Profile adminProfile = createProfile("Administrador", 'A', 'N', 'S');
        Profile userProfile  = createProfile("Usuario", 'A', 'S', 'N');

        // ── Modules & Submodules Setup ───────────────────────────────────────
        // name = texto en español para mostrar en la UI
        // url  = en inglés y minúscula, debe coincidir EXACTO con la ruta del front

        // 1. MÓDULO: Hoteles
        Module hotelsModule = createModule("Hoteles", "/hotels", "Gestión de Hoteles", 'A');
        setupPermissionsForModule(adminProfile, hotelsModule,
                List.of(new SubModuleSpec("Hoteles", "hotels")));

        // 2. MÓDULO: Catálogos / Configuración
        Module catalogModule = createModule("Catálogos", "/catalog", "Gestión de Catálogos y Ubicaciones", 'A');
        List<SubModuleSpec> catalogSubmodules = List.of(
                new SubModuleSpec("Países", "countries"),
                new SubModuleSpec("Departamentos", "departments"),
                new SubModuleSpec("Ciudades", "cities"),
                new SubModuleSpec("Categorías", "categories"),
                new SubModuleSpec("Características", "features")
        );
        setupPermissionsForModule(adminProfile, catalogModule, catalogSubmodules);

        // 3. MÓDULO: Usuarios y Seguridad
        Module securityModule = createModule("Usuarios y Seguridad", "/security", "Gestión de Usuarios y Roles", 'A');
        List<SubModuleSpec> securitySubmodules = List.of(
                new SubModuleSpec("Usuarios", "users")
        );
        setupPermissionsForModule(adminProfile, securityModule, securitySubmodules);

        // 4. MÓDULO: Mi Cuenta -> solo Actualizar (U)
        Module profileModule = createModule("Mi Cuenta", "/userProfile", "Perfil de Usuario", 'A');
        List<SubModuleSpec> profileSubmodules = List.of(
                new SubModuleSpec("Mi Perfil", "userProfile")
        );
        setupPermissionsForModule(adminProfile, profileModule, profileSubmodules, List.of('U'));
        setupPermissionsForModule(userProfile, profileModule, profileSubmodules, List.of('U'));

        // 5. MÓDULO: Perfiles
        Module profilesModule = createModule("Panel de control", "/profiles", "Perfiles", 'A');
        List<SubModuleSpec> profilesSubmodules = List.of(
                new SubModuleSpec("Perfiles", "profiles")
        );
        setupPermissionsForModule(adminProfile, profilesModule, profilesSubmodules);

        // ── Users ────────────────────────────────────────────────────────────
        createUser("Cristian", "Alexander", "admin@sweetnest.com", "Admin123", adminProfile);
        createUser("Juan", "Pérez", "usuario@sweetnest.com", "Usuario123", userProfile);

        // ── Countries ────────────────────────────────────────────────────────
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
        Department jalisco      = CreateDepartment("Jalisco",          mexico);

        // ── Categories ──────────────────────────────────────────────────────
        Category playa = createCategory("Playa");
        Category boutique = createCategory("Boutique");
        Category economico = createCategory("Económico");

        // ── Features ──────────────────────────────────────────────────────
        Feature pool = createFeature("Piscina", 'A');
        Feature wifi = createFeature("Wi-Fi", 'A');
        Feature parking = createFeature("Parqueadero", 'A');

        // ── Cities (Colombia) ───────────────────────────────────────────────

        City bogota        = createCity("Bogotá", cundinamarca);
        City cali           = createCity("Cali",            valle);
        City barranquilla   = createCity("Barranquilla",    atlantico);
        City soledad        = createCity("Soledad",          atlantico);
        City medellin       = createCity("Medellín",         antioquia);
        City bucaramanga    = createCity("Bucaramanga",      santander);

        // ── Cities (Argentina) ──────────────────────────────────────────────

        City buenosAiresCity = createCity("Buenos Aires",    buenosAires);
        City laPlata         = createCity("La Plata",        buenosAires);

        // ── Cities (México) ─────────────────────────────────────────────────

        City ciudadDeMexico  = createCity("Ciudad de México", cdmx);
        City guadalajara     = createCity("Guadalajara", jalisco);

        // ── Hotels ──────────────────────────────────────────────────────────
        createHotel("Hotel Poblado Suite",
                "Un hermoso hotel boutique en el corazón de El Poblado.",
                "Cra 43A #9-12, El Poblado",
                250000, medellin,
                List.of(boutique),
                List.of(pool, parking));

        createHotel("Hotel Guadalajara Colonial",
                "Disfruta del auténtico estilo jalisciense con mariachis y confort.",
                "Av. Vallarta #2340",
                120000, guadalajara,
                List.of(boutique, economico),
                List.of(pool, parking));

        createHotel("Hotel Chicamocha Real",
                "Vista espectacular a la ciudad y la mejor comodidad santandereana.",
                "Calle 34 #28-45, Centro",
                180000, bucaramanga,
                List.of(economico),
                List.of(pool, parking, wifi));

        createHotel("Hostal Andino Bogotá",
                "Ambiente acogedor y económico, ideal para viajeros que exploran la ciudad.",
                "Cra 7 #12-34, La Candelaria",
                90000, bogota,
                List.of(economico),
                List.of(pool, parking, wifi));

        createHotel("Costa Caribe Barranquilla",
                "Frente al río, con piscina y terraza para disfrutar del clima costeño.",
                "Cra 51B #79-45, El Prado",
                210000, barranquilla,
                List.of(playa, boutique),
                List.of(pool, parking, wifi));

        createHotel("Hotel Valle Real Cali",
                "Elegancia y confort en el corazón de la capital salsera.",
                "Av. 6N #23-10, Granada",
                160000, cali,
                List.of(boutique),
                List.of(pool));

        createHotel("Soledad Inn Express",
                "Opción práctica y económica cerca al aeropuerto.",
                "Cl 30 #19-50, Centro",
                75000, soledad,
                List.of(economico),
                List.of(wifi));

        createHotel("Buenos Aires Palace Hotel",
                "Lujo clásico porteño a pasos del Obelisco.",
                "Av. Corrientes 1234",
                300000, buenosAiresCity,
                List.of(boutique),
                List.of(parking, wifi));

        createHotel("La Plata Garden Hotel",
                "Tranquilidad y jardines en una de las ciudades más verdes de Argentina.",
                "Calle 50 #650",
                140000, laPlata,
                List.of(economico, boutique),
                List.of(pool, wifi));

        createHotel("Ciudad de México Grand Hotel",
                "Ubicación privileged cerca del Zócalo, con vistas panorámicas.",
                "Av. Juárez 88, Centro Histórico",
                280000, ciudadDeMexico,
                List.of(boutique, playa),
                List.of(pool, wifi));
    }

    private void setupPermissionsForModule(Profile profile, Module module, List<SubModuleSpec> submodules) {
        setupPermissionsForModule(profile, module, submodules, FULL_CRUD);
    }

    private void setupPermissionsForModule(Profile profile, Module module, List<SubModuleSpec> submodules, List<Character> actions) {

        PermissionProfilesId ppId = new PermissionProfilesId(profile.getId(), module.getId());
        PermissionProfiles pp = PermissionProfiles.builder()
                .id(ppId)
                .profile(profile)
                .module(module)
                .entryAllowed('S')
                .build();
        permissionProfilesRepository.save(pp);

        for (SubModuleSpec spec : submodules) {

            SubModule subModule = getOrCreateSubModule(module, spec);

            PermissionProfilesModuleId ppmId =
                    new PermissionProfilesModuleId(profile.getId(), module.getId(), subModule.getId());
            PermissionProfilesModule ppm = PermissionProfilesModule.builder()
                    .id(ppmId)
                    .profile(profile)
                    .module(module)
                    .subModule(subModule)
                    .permissionProfiles(pp)
                    .build();
            permissionProfilesModuleRepository.save(ppm);

            for (Character code : actions) {

                PermissionProfilesModulePerId ppmPerId = new PermissionProfilesModulePerId(
                        profile.getId(),
                        module.getId(),
                        subModule.getId(),
                        code
                );

                PermissionProfilesModulePer ppmPer = PermissionProfilesModulePer.builder()
                        .id(ppmPerId)
                        .permissionProfilesModule(ppm)
                        .actionName(actionShortLabel(code))
                        .actionDescription(actionLabel(code) + " en " + spec.name())
                        .check('S')
                        .build();

                permissionProfilesModulePerRepository.save(ppmPer);
            }
        }
    }

    private SubModule getOrCreateSubModule(Module module, SubModuleSpec spec) {
        return subModuleRepository.findByModuleIdAndName(module.getId(), spec.name())
                .orElseGet(() -> {
                    SubModule subModule = SubModule.builder()
                            .module(module)
                            .name(spec.name())
                            .url("/" + spec.url())
                            .description(null)
                            .state('A')
                            .build();
                    return subModuleRepository.save(subModule);
                });
    }

    private Profile createProfile(String name, Character state, Character isDefault, Character hasControlAccess) {

        Profile profile = Profile.builder()
                .name(name)
                .state(state)
                .isDefault(isDefault)
                .hasControlAccess(hasControlAccess)
                .build();
        return IProfileRepository.save(profile);
    }

    private Module createModule(String name, String url, String description, Character state) {
        Module module = Module.builder()
                .name(name)
                .url(url)
                .description(description)
                .state(state)
                .build();
        return moduleRepository.save(module);
    }

    private void createUser(String firstName, String lastName, String email, String rawPassword, Profile profile) {
        User user = User.builder()
                .userFirstName(firstName)
                .userLastName(lastName)
                .userEmail(email)
                .userPass(passwordEncoder.encode(rawPassword))
                .profile(profile)
                .build();
        userRepository.save(user);
    }

    private Department CreateDepartment(String name, Pais country) {
        Department dep = new Department();
        dep.setDepName(name);
        dep.setPais(country);
        dep.setDepState('A');
        return departmentRepository.save(dep);
    }

    private City createCity(String name, Department department) {
        City city = new City();
        city.setCitName(name);
        city.setDepartment(department);
        city.setCitState('A');
        return cityRepository.save(city);
    }

    private Category createCategory(String catName) {
        Category category = new Category();
        category.setCatName(catName);
        category.setCatEst('A');
        return categoryRepository.save(category);
    }

    private void createHotel(String name, String description, String address, int cost, City city,
                             List<Category> hotelCategories, List<Feature> hotelFeatures) {

        Hotel hotel = new Hotel();

        hotel.setHotName(name);
        hotel.setHotDescription(description);
        hotel.setHotAddress(address);
        hotel.setHotCost(cost);
        hotel.setCity(city);
        hotel.setHotState('A');
        hotel.setCategories(new ArrayList<>(hotelCategories));
        hotel.setFeatures(new ArrayList<>(hotelFeatures));

        hotelRepository.save(hotel);
    }

    private Feature createFeature(String name, Character state) {

        Feature feature = new Feature();
        feature.setFeaName(name);
        feature.setFeaEst(state);

        return featureRepository.save(feature);
    }

    private String actionShortLabel(Character code) {
        return switch (code) {
            case 'R' -> "Listar";
            case 'C' -> "Agregar";
            case 'U' -> "Editar";
            case 'D' -> "Borrar";
            default -> "Desconocido";
        };
    }

    private String actionLabel(Character code) {
        return switch (code) {
            case 'R' -> "Permiso de Listar";
            case 'C' -> "Permiso de Agregar";
            case 'U' -> "Permiso de Actualizar";
            case 'D' -> "Permiso de Eliminar";
            default -> "Permiso desconocido";
        };
    }
}