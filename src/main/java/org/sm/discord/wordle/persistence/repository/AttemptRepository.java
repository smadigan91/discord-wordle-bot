package org.sm.discord.wordle.persistence.repository;

import org.sm.discord.wordle.persistence.entity.Attempt;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttemptRepository extends CrudRepository<Attempt, Long> {

    boolean existsByUserIdAndProblemId(String userId, String problemId);

    Optional<Attempt> findByUserIdAndProblemId(String userId, String problemId);

    //best attempts
    List<Attempt> findAllByOrderByScoreDesc();

    //worst attempts
    List<Attempt> findAllByOrderByScoreAsc();

}