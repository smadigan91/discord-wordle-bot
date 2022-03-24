package org.sm.discord.wordle.persistence.repository;

import org.sm.discord.wordle.persistence.entity.CalculableEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface CalculableEntityRepository<T extends CalculableEntity> extends CrudRepository<T, String> {

    List<T> findAllByOrderByAverageScoreDesc();

    List<T> findAllByOrderByAverageGuessesDesc();

    List<T> findAllByOrderByTotalAttemptsDesc();

    List<T> findAllByOrderByTotalGuessesDesc();

}