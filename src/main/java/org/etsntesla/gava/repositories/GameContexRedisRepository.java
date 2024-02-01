package org.etsntesla.gava.repositories;

import org.etsntesla.gava.models.GameContext;
import org.etsntesla.gava.utils.CustomRedisTemplate;
import org.etsntesla.gava.utils.StackTraceMessage;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Repository;


import java.util.*;
import java.util.stream.Collectors;


@Repository
public class GameContexRedisRepository implements GameContextRepository{

    final static protected String GAMES="games";
    final protected CustomRedisTemplate redisTemplate;

    protected long offset=0l;

    protected long count=10l;

    public long getOffset() {
        return offset;
    }

    public long getOffsetAndAscend(){
        long old = offset;
        offset+=count;
        return old;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public GameContexRedisRepository(CustomRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    @Override
    public <S extends GameContext> S save(S entity) {
        final String key = String.valueOf(entity.getGAME_ID());
        final GameContext gameContext = entity;
        SessionCallback<GameContext> callback = new SessionCallback<GameContext>() {
            @Override
            public <K, V> GameContext execute(RedisOperations<K, V> operations) throws DataAccessException {
                operations.multi();
                redisTemplate.opsForValue().set(key,gameContext);
                redisTemplate.boundZSetOps(GAMES).add(String.valueOf(gameContext.getGAME_ID()),gameContext.getPlayers().size());
                List<?> result = operations.exec();
                if(!isAllDoneCorrect(result)) throw new RDataAccessException("Redis fail to save");
                return (GameContext) redisTemplate.opsForValue().get(key);
            }
        };
        return (S)redisTemplate.execute(callback);
    }


    @Override
    public <S extends GameContext> Iterable<S> saveAll(Iterable<S> entities) {
        SessionCallback<Boolean> callback = new SessionCallback<Boolean>() {
            @Override
            public <K, V> Boolean execute(RedisOperations<K, V> operations) throws DataAccessException {
                operations.multi();
                for (S entity:entities) {
                    String gameID = String.valueOf(entity.getGAME_ID());
                    redisTemplate.opsForValue().set(gameID,entity);
                    redisTemplate.boundZSetOps(GAMES).add(gameID,entity.getPlayers().size());
                }
                List<?> result = operations.exec();
                return isAllDoneCorrect(result);
                }
            };
            redisTemplate.execute(callback);
            return entities;
    }




    @Override
    public Optional<GameContext> findById(String s) {
        if (redisTemplate.hasKey(s).booleanValue())
            return Optional.of((GameContext) redisTemplate.opsForValue().get(s));
        else
            return Optional.empty();
    }

    @Override
    public boolean existsById(String s) {
        return findById(s).isPresent();
    }

    @Override
    public Iterable<GameContext> findAll() {
        return findAll(offset,count);
    }

    public Iterable<GameContext> findAll(Long offset, Long count) {
        List<?> gamesId = redisTemplate.boundZSetOps(GAMES).range(offset,offset+count-1)
                .stream()
                .toList();
        return findAllById((List<String>)gamesId);
    }


    @Override
    public Iterable<GameContext> findAllById(Iterable<String> strings) {
        List<?> gameContexts = redisTemplate.opsForValue().multiGet((Collection<String>) strings)
                .stream()
                .filter(e->Objects.nonNull(e))
                .toList();
        return (List<GameContext>)gameContexts;
    }

    @Override
    public long count() {
        return count(0,100);
    }

    public long count(int numPlayers){
        return count(numPlayers,numPlayers);
    }

    public long count(int minPlayers,int maxPlayers){
        return redisTemplate.boundZSetOps(GAMES).count(minPlayers,maxPlayers);
    }


    @Override
    public void deleteById(String s) {
        SessionCallback<Boolean> callback = new SessionCallback<Boolean>() {
            @Override
            public <K, V> Boolean execute(RedisOperations<K, V> operations) throws DataAccessException {
                operations.multi();
                redisTemplate.boundZSetOps(GAMES).remove(s);
                redisTemplate.delete(s);
                List<?> result = operations.exec();
                return isAllDoneCorrect(result);
            }
        };
        redisTemplate.execute(callback);
    }

    @Override
    public void delete(GameContext entity) {
        final String gameId = String.valueOf(entity.getGAME_ID());
        deleteById(gameId);
    }

    @Override
    public void deleteAllById(Iterable<? extends String> strings) {
        SessionCallback<Boolean> callback = new SessionCallback<Boolean>() {
            @Override
            public <K, V> Boolean execute(RedisOperations<K, V> operations) throws DataAccessException {
                operations.multi();
                redisTemplate.delete((Collection<String>) strings);
                redisTemplate.boundZSetOps(GAMES).remove( ((Collection<? extends String>) strings).stream().toArray());
                List<?> result = operations.exec();
                return  isAllDoneCorrect(result);
            }
        };
        redisTemplate.execute(callback);
    }

    @Override
    public void deleteAll(Iterable<? extends GameContext> entities) {
        final List<String> gamesId = new ArrayList<>();
        entities.forEach(e -> gamesId.add(String.valueOf(e.getGAME_ID())));
        deleteAllById(gamesId);
    }

    @Override
    public void deleteAll() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    static class RDataAccessException extends DataAccessException{

        public RDataAccessException(String msg) {
            super(msg);
        }

        public RDataAccessException(String msg, Throwable cause) {
            super(msg, cause);
        }

        @Override
        public String getMessage() {
            return StackTraceMessage.message(super.getMessage());
        }
    }

    private Boolean isAllDoneCorrect(List<? extends  Object> resultsExpectations){
        //TO DO
        //Za brisanje vraca koliko je obrisano, za druge operacije true ili false za uspeh
        return true;

    }
}
