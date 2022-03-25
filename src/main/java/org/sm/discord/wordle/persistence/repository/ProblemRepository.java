package org.sm.discord.wordle.persistence.repository;

import org.sm.discord.wordle.persistence.entity.Problem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemRepository extends CalculableEntityRepository<Problem> {

    List<Problem> findBySolutionIsNull();

}