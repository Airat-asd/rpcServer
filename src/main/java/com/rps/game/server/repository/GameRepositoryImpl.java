package com.rps.game.server.repository;

import com.rps.game.server.model.Game;
import com.rps.game.server.model.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Optional;

@Slf4j
@Repository
public class GameRepositoryImpl implements GameRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional(readOnly = true)
    public Game getGameById(String id) {
        return em.find(Game.class, id);
    }

    @Override
    @Transactional
    public void insert(Game game) {
        em.persist(game);
    }

    @Override
    @Transactional
    public Game update(Game game) {
        return em.merge(game);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Game> getGameByPlayer(Player player) {
        Optional<Game> gameOptional;
        try {
            TypedQuery<Game> typedQuery = em.createQuery("select p from Game p where p.player1 = :player or p.player2 = :player", Game.class);
            typedQuery.setParameter("player", player);
            gameOptional = Optional.ofNullable(typedQuery.getSingleResult());
        } catch (NoResultException e) {
            log.info(e.getMessage());
            gameOptional = Optional.empty();
        }

        return gameOptional;
    }

    @Override
    @Transactional
    public void deleteGame(Game game) {
        Game mergeGame = em.merge(game);
        em.remove(mergeGame);
    }
}