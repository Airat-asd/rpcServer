package com.rps.game.server.repository;

import com.rps.game.server.model.Player;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.*;

@Slf4j
@Repository
@Transactional(readOnly = true)
public class PlayerRepositoryImpl implements PlayerRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public Player insert(Player player) {
        return em.merge(player);
    }

    @Override
    public Optional<Player> getPlayerBySession(ChannelHandlerContext ctx) {
        Optional<Player> playerOptional;
        try {
            TypedQuery<Player> typedQuery = em.createQuery("select p from Player p where p.session = :session", Player.class);
            typedQuery.setParameter("session", ctx.channel().id().asLongText());
            playerOptional = Optional.ofNullable(typedQuery.getSingleResult());
        } catch (NoResultException e) {
            log.info(e.getMessage());
            playerOptional = Optional.empty();
        }

        return playerOptional;
    }
}