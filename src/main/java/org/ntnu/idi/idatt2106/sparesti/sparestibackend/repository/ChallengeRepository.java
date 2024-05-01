package org.ntnu.idi.idatt2106.sparesti.sparestibackend.repository;

import java.util.List;
import java.util.Optional;
import org.ntnu.idi.idatt2106.sparesti.sparestibackend.model.Challenge;
import org.ntnu.idi.idatt2106.sparesti.sparestibackend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    Page<Challenge> findByUser(User user, Pageable pageable);

    Optional<Challenge> findByIdAndUser(Long id, User user);

    Page<Challenge> findAllByCompletedOnIsNullAndUser(User user, Pageable pageable);

    List<Challenge> findAllByCompletedOnIsNullAndUser(User user);

    Page<Challenge> findAllByCompletedOnIsNotNullAndUser(User user, Pageable pageable);
}
