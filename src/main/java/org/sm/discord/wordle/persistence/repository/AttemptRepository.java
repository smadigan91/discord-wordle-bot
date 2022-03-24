package org.sm.discord.wordle.persistence.repository;

import org.sm.discord.wordle.persistence.entity.Attempt;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttemptRepository extends CrudRepository<Attempt, Long> {

    boolean existsByUserIdAndProblemId(String userId, String problemId);

    //best attempts
    Attempt findAllByOrderByScoreDesc();

    //worst attempts
    Attempt findAllByOrderByScoreAsc();

}