package com.cinema.booking.config;

import com.cinema.booking.entity.*;
import com.cinema.booking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final GenreRepository genreRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileRepository profileRepository;
    private final SeatRepository seatRepository;
    private final ShowtimeRepository showtimeRepository;

    @Override
    public void run(String... args) throws Exception {
        seedRoles();
        seedGenres();
        seedRoomsAndSeats();
        forceCreateAdmin("admin@gmail.com", "admin123");
        forceCreateAdmin("admin2@gmail.com", "admin123");
        forceCreateStaff("staff@gmail.com", "staff123");
        seedMoviesAndShowtimes();
    }

    private void seedRoles() {
        List<String> roleNames = Arrays.asList("ROLE_ADMIN", "ROLE_STAFF", "ROLE_CUSTOMER");
        for (String name : roleNames) {
            if (!roleRepository.findByName(name).isPresent()) {
                Role role = new Role();
                role.setName(name);
                roleRepository.save(role);
            }
        }
    }

    private void seedGenres() {
        if (genreRepository.count() == 0) {
            List<String> genres = Arrays.asList("Action", "Horror", "Comedy", "Drama", "Sci-Fi");
            genres.forEach(name -> {
                Genre genre = new Genre();
                genre.setName(name);
                genreRepository.save(genre);
            });
        }
    }

    private void seedRoomsAndSeats() {
        if (roomRepository.count() == 0) {
            for (int i = 1; i <= 2; i++) {
                Room room = new Room();
                room.setName("Room " + i);
                room.setTotalSeats(20);
                Room savedRoom = roomRepository.save(room);

                for (int j = 1; j <= 20; j++) {
                    Seat seat = new Seat();
                    seat.setSeatNumber((j <= 10 ? "A" : "B") + (j % 10 == 0 ? 10 : j % 10));
                    seat.setRoom(savedRoom);
                    seatRepository.save(seat);
                }
            }
        }
    }

    private void forceCreateAdmin(String email, String password) {
        Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow();
        Optional<User> existingUser = userRepository.findByEmail(email);
        
        User admin;
        if (existingUser.isPresent()) {
            admin = existingUser.get();
        } else {
            admin = new User();
            admin.setUsername(email.split("@")[0]);
            admin.setEmail(email);
        }
        
        admin.setPassword(passwordEncoder.encode(password));
        admin.setRole(adminRole);
        User savedAdmin = userRepository.save(admin);

        if (!profileRepository.findByUser(savedAdmin).isPresent()) {
            Profile profile = new Profile();
            profile.setUser(savedAdmin);
            profile.setFullName("Administrator " + admin.getUsername());
            profileRepository.save(profile);
        }
        System.out.println(">>> Force Update Admin: " + email + " / " + password);
    }

    private void forceCreateStaff(String email, String password) {
        Role staffRole = roleRepository.findByName("ROLE_STAFF").orElseThrow();
        Optional<User> existingUser = userRepository.findByEmail(email);
        User staff;
        if (existingUser.isPresent()) {
            staff = existingUser.get();
        } else {
            staff = new User();
            staff.setUsername(email.split("@")[0]);
            staff.setEmail(email);
        }

        staff.setPassword(passwordEncoder.encode(password));
        staff.setRole(staffRole);
        User savedStaff = userRepository.save(staff);

        if (!profileRepository.findByUser(savedStaff).isPresent()) {
            Profile profile = new Profile();
            profile.setUser(savedStaff);
            profile.setFullName("Staff " + staff.getUsername());
            profileRepository.save(profile);
        }
        System.out.println(">>> Force Create Staff: " + email + " / " + password);
    }

    private void seedMoviesAndShowtimes() {
        if (movieRepository.count() == 0) {
            Genre action = genreRepository.findAll().get(0);
            Room room1 = roomRepository.findAll().get(0);
            
            Movie m1 = new Movie();
            m1.setTitle("Avenger: Endgame");
            m1.setDescription("Phim siêu anh hùng Marvel đình đám.");
            m1.setDuration(180);
            m1.setGenre(action);
            m1.setReleaseDate(LocalDate.now());
            m1.setPoster("https://m.media-amazon.com/images/M/MV5BMTc5MDE2ODcwNV5BMl5BanBnXkFtZTgwMzI2NzQ2NzM@._V1_.jpg");
            Movie savedMovie = movieRepository.save(m1);

            Showtime st = new Showtime();
            st.setMovie(savedMovie);
            st.setRoom(room1);
            st.setStartTime(LocalDateTime.now().plusDays(1).withHour(19).withMinute(0));
            st.setEndTime(st.getStartTime().plusMinutes(180 + 15));
            showtimeRepository.save(st);
        }
    }
}
