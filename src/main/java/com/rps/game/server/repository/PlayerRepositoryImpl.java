package com.rps.game.server.repository;

import com.rps.game.server.model.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.*;

@Slf4j
@Repository
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
@Transactional(readOnly = true)
public class PlayerRepositoryImpl implements PlayerRepository {

    @PersistenceContext
    private EntityManager em;
//    private final Map<ChannelHandlerContext, Player> playerMap = new HashMap<>();

    @Override
    @Transactional
//    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public Player insert(Player player) {
        log.info("insert(Player player) {}", player);
        return em.merge(player);
//        playerMap.put(player.getSession(), player);
    }

//    @Override
//    public Optional<Player> getByContext(ChannelHandlerContext ctx) {
//        return Optional.ofNullable(playerMap.get(ctx));
//    }

    @Override
    public Optional<Player> getByName(String name) {
        log.info("getByName(String name)");
        Optional<Player> playerOptional;
        try {
            TypedQuery<Player> typedQuery = em.createQuery("select p from Player p where p.name = :name", Player.class);
            typedQuery.setParameter("name", name);
            playerOptional = Optional.ofNullable(typedQuery.getSingleResult());
        } catch (NoResultException e) {
            log.info(e.getMessage());
            playerOptional = Optional.empty();
        }

        return playerOptional;
    }
}