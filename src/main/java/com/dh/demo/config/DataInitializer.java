package com.dh.demo.config;

import com.dh.demo.entity.*;
import com.dh.demo.entity.Module;
import com.dh.demo.entity.enums.ReservationStatus;
import com.dh.demo.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
    private final HotelTypeRepository hotelTypeRepository;
    private final HotelUnitRepository hotelUnitRepository;
    private final ReservationRepository reservationRepository;
    private final FavoriteRepository favoriteRepository;
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
                           HotelTypeRepository hotelTypeRepository,
                           HotelUnitRepository hotelUnitRepository,
                           ReservationRepository reservationRepository,
                           FavoriteRepository favoriteRepository,
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
        this.hotelTypeRepository = hotelTypeRepository;
        this.hotelUnitRepository = hotelUnitRepository;
        this.reservationRepository = reservationRepository;
        this.favoriteRepository = favoriteRepository;
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
        List<SubModuleSpec> hotelsSubmodules = List.of(
                new SubModuleSpec("Hoteles", "hotels"),
                new SubModuleSpec("Tipos", "type-hotels")
        );
        setupPermissionsForModule(adminProfile, hotelsModule, hotelsSubmodules);

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

        // 4b. Mi Cuenta -> Mis favoritos: ver (R) y quitar (D). Todo usuario logueado lo debe tener;
        // los registros nuevos lo heredan del perfil default "Usuario".
        List<SubModuleSpec> favoritesSubmodules = List.of(
                new SubModuleSpec("Mis favoritos", "favorites")
        );
        setupPermissionsForModule(adminProfile, profileModule, favoritesSubmodules, List.of('R', 'D'));
        setupPermissionsForModule(userProfile, profileModule, favoritesSubmodules, List.of('R', 'D'));

        // 5. MÓDULO: Perfiles
        Module profilesModule = createModule("Panel de control", "/profiles", "Perfiles", 'A');
        List<SubModuleSpec> profilesSubmodules = List.of(
                new SubModuleSpec("Perfiles", "profiles")
        );
        setupPermissionsForModule(adminProfile, profilesModule, profilesSubmodules);

        // ── Users ────────────────────────────────────────────────────────────
        User adminUser = createUser("Cristian", "Alexander", "admin@sweetnest.com", "Admin123", adminProfile);
        User guestUser = createUser("Juan", "Pérez", "usuario@sweetnest.com", "Usuario123", userProfile);
        // Anfitriones (Fase 2): userHost='S' para poder ser asignados como host de un hotel
        User hostUserA = createUser("Laura", "Gómez", "anfitrion@sweetnest.com", "Anfitrion123", userProfile);
        User hostUserB = createUser("Carlos", "Rivas", "anfitrion2@sweetnest.com", "Anfitrion123", userProfile);
        hostUserA.setUserHost('S');
        hostUserB.setUserHost('S');
        userRepository.save(hostUserA);
        userRepository.save(hostUserB);

        // ── Hotel types (ANTES que los hoteles: hotTypSec es FK obligatoria) ─
        HotelType typeHotel = createHotelType("Hotel", 'S');
        HotelType typeCasa  = createHotelType("Casa", 'N');
        HotelType typeDepto = createHotelType("Apartamento", 'N');

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

        // ── Hotels (tipo + politica de reserva + su unidad) ─────────────────
        Hotel poblado = createHotel("Hotel Poblado Suite",
                "Un hermoso hotel boutique en el corazón de El Poblado.",
                "Cra 43A #9-12, El Poblado",
                250000, medellin, typeHotel, 'N', 2, 4,
                List.of(boutique), List.of(pool, parking));
        Hotel guadalajaraH = createHotel("Hotel Guadalajara Colonial",
                "Disfruta del auténtico estilo jalisciense con mariachis y confort.",
                "Av. Vallarta #2340",
                120000, guadalajara, typeHotel, 'S', 3, 4,
                List.of(boutique, economico), List.of(pool, parking));
        Hotel chicamocha = createHotel("Hotel Chicamocha Real",
                "Vista espectacular a la ciudad y la mejor comodidad santandereana.",
                "Calle 34 #28-45, Centro",
                180000, bucaramanga, typeHotel, 'S', 2, 3,
                List.of(economico), List.of(pool, parking, wifi));
        Hotel hostalAndino = createHotel("Hostal Andino Bogotá",
                "Ambiente acogedor y económico, ideal para viajeros que exploran la ciudad.",
                "Cra 7 #12-34, La Candelaria",
                90000, bogota, typeHotel, 'N', 1, 2,
                List.of(economico), List.of(pool, parking, wifi));
        Hotel costaCaribe = createHotel("Costa Caribe Barranquilla",
                "Frente al río, con piscina y terraza para disfrutar del clima costeño.",
                "Cra 51B #79-45, El Prado",
                210000, barranquilla, typeCasa, 'N', 6, 4,
                List.of(playa, boutique), List.of(pool, parking, wifi));
        Hotel valleReal = createHotel("Hotel Valle Real Cali",
                "Elegancia y confort en el corazón de la capital salsera.",
                "Av. 6N #23-10, Granada",
                160000, cali, typeHotel, 'S', 2, 4,
                List.of(boutique), List.of(pool));
        Hotel soledadInn = createHotel("Soledad Inn Express",
                "Opción práctica y económica cerca al aeropuerto.",
                "Cl 30 #19-50, Centro",
                75000, soledad, typeHotel, 'N', 1, 2,
                List.of(economico), List.of(wifi));
        Hotel buenosAiresPalace = createHotel("Buenos Aires Palace Hotel",
                "Lujo clásico porteño a pasos del Obelisco.",
                "Av. Corrientes 1234",
                300000, buenosAiresCity, typeHotel, 'S', 2, 4,
                List.of(boutique), List.of(parking, wifi));
        Hotel laPlataGarden = createHotel("La Plata Garden Hotel",
                "Tranquilidad y jardines en una de las ciudades más verdes de Argentina.",
                "Calle 50 #650",
                140000, laPlata, typeDepto, 'S', 4, 4,
                List.of(economico, boutique), List.of(pool, wifi));
        Hotel cdmxGrand = createHotel("Ciudad de México Grand Hotel",
                "Ubicación privileged cerca del Zócalo, con vistas panorámicas.",
                "Av. Juárez 88, Centro Histórico",
                280000, ciudadDeMexico, typeHotel, 'N', 2, 4,
                List.of(boutique, playa), List.of(pool, wifi));

        // ── Anfitriones (Fase 2): cada hotel queda con su host (hotUserSec) ──
        assignHost(poblado, hostUserA);
        assignHost(guadalajaraH, hostUserB);
        assignHost(chicamocha, hostUserA);
        assignHost(hostalAndino, hostUserB);
        assignHost(costaCaribe, hostUserA);
        assignHost(valleReal, hostUserB);
        assignHost(soledadInn, hostUserA);
        assignHost(buenosAiresPalace, hostUserB);
        assignHost(laPlataGarden, hostUserA);
        assignHost(cdmxGrand, hostUserB);

        // ── Extra units para los hoteles multi-unidad (tipo 'S') ────────────
        createUnit(guadalajaraH, "Habitación 101", 4, 120000);
        createUnit(guadalajaraH, "Habitación 102", 2, 95000);
        createUnit(chicamocha,   "Habitación 301", 3, 180000);
        createUnit(chicamocha,   "Habitación 302", 2, 140000);
        createUnit(valleReal,    "Suite 1201",     4, 160000);
        createUnit(buenosAiresPalace, "Habitación 701", 4, 300000);
        createUnit(laPlataGarden, "Apartamento 1A", 4, 140000);
        createUnit(laPlataGarden, "Apartamento 1B", 2, 110000);

        // ── Sample reservations ─────────────────────────────────────────────
        // hoteles con hotConfirmReq='N' -> nacen CONFIRMED
        createReservation(guestUser, firstUnitOf(poblado),        plusDays(3),  plusDays(6),  2, ReservationStatus.CONFIRMED);
        createReservation(guestUser, firstUnitOf(costaCaribe),    plusDays(10), plusDays(14), 4, ReservationStatus.CONFIRMED);
        createReservation(adminUser, firstUnitOf(hostalAndino),   plusDays(5),  plusDays(8),  1, ReservationStatus.CONFIRMED);
        // hoteles con hotConfirmReq='S' -> nacen PENDING (esperan al administrador)
        createReservation(guestUser, firstUnitOf(guadalajaraH),   plusDays(7),  plusDays(11), 3, ReservationStatus.PENDING);
        createReservation(guestUser, firstUnitOf(chicamocha),     plusDays(20), plusDays(23), 2, ReservationStatus.PENDING);
        createReservation(guestUser, firstUnitOf(laPlataGarden),  plusDays(15), plusDays(19), 4, ReservationStatus.PENDING);
        // un CANCELLED para probar que no bloquea
        createReservation(guestUser, firstUnitOf(valleReal),      plusDays(2),  plusDays(5),  2, ReservationStatus.CANCELLED);

        // ── Sample favorites (despues de usuarios y hoteles) ────────────────
        createFavorite(guestUser, poblado);
        createFavorite(guestUser, costaCaribe);
    }

    private void createFavorite(User user, Hotel hotel) {
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setHotel(hotel);
        favoriteRepository.save(favorite);
    }

    private LocalDate plusDays(int days) {
        return LocalDate.now().plusDays(days);
    }

    private HotelUnit firstUnitOf(Hotel hotel) {
        return hotel.getUnits().get(0);
    }

    private void createReservation(User user, HotelUnit unit, LocalDate checkIn, LocalDate checkOut,
                                   int guests, ReservationStatus status) {

        int nights = (int) ChronoUnit.DAYS.between(checkIn, checkOut);

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setUnit(unit);
        reservation.setResCheckIn(checkIn);
        reservation.setResCheckOut(checkOut);
        reservation.setResGuests(guests);
        reservation.setResPrice(unit.getUniPrice());
        reservation.setResTotal(nights * unit.getUniPrice());
        reservation.setResStatus(status);

        reservationRepository.save(reservation);
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

    private User createUser(String firstName, String lastName, String email, String rawPassword, Profile profile) {
        User user = User.builder()
                .userFirstName(firstName)
                .userLastName(lastName)
                .userEmail(email)
                .userPass(passwordEncoder.encode(rawPassword))
                .profile(profile)
                .build();
        return userRepository.save(user);
    }

    private HotelType createHotelType(String name, Character multiUnit) {
        HotelType type = new HotelType();
        type.setTypName(name);
        type.setTypMultiUnit(multiUnit);
        type.setTypState('A');
        return hotelTypeRepository.save(type);
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

    private Hotel createHotel(String name, String description, String address, int cost, City city,
                              HotelType type, Character confirmReq, int unitCapacity, int unitPrice,
                              List<Category> hotelCategories, List<Feature> hotelFeatures) {

        Hotel hotel = new Hotel();

        hotel.setHotName(name);
        hotel.setHotDescription(description);
        hotel.setHotAddress(address);
        hotel.setHotCost(cost);
        hotel.setCity(city);
        hotel.setType(type);
        hotel.setHotConfirmReq(confirmReq);
        hotel.setHotState('A');
        hotel.setCategories(new ArrayList<>(hotelCategories));
        hotel.setFeatures(new ArrayList<>(hotelFeatures));

        // Toda unidad de reserva: en tipos de una sola unidad se crea automatica
        // con el nombre del alojamiento.
        HotelUnit unit = new HotelUnit();
        unit.setHotel(hotel);
        unit.setUniName(name);
        unit.setUniCapacity(unitCapacity);
        unit.setUniPrice(unitPrice);
        unit.setUniState('A');
        hotel.getUnits().add(unit);

        return hotelRepository.save(hotel);
    }

    /** Fase 2: asigna el anfitrion de un hotel (hotUserSec). */
    private void assignHost(Hotel hotel, User host) {
        hotel.setHost(host);
        hotelRepository.save(hotel);
    }

    private void createUnit(Hotel hotel, String name, int capacity, int price) {

        HotelUnit unit = new HotelUnit();
        unit.setHotel(hotel);
        unit.setUniName(name);
        unit.setUniCapacity(capacity);
        unit.setUniPrice(price);
        unit.setUniState('A');

        hotelUnitRepository.save(unit);
        hotel.getUnits().add(unit);
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