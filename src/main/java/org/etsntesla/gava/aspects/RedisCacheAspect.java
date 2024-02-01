package org.etsntesla.gava.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.etsntesla.gava.models.Game;
import org.etsntesla.gava.utils.CustomRedisTemplate;
import org.etsntesla.gava.utils.Provisions;
import org.etsntesla.gava.utils.annotations.AppCacheArg;
import org.etsntesla.gava.utils.annotations.PreferenceRedisCache;
import org.etsntesla.gava.utils.enums.PreferenceType;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import reactor.util.annotation.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;


@Component
@Aspect
public class RedisCacheAspect{


    private final CustomRedisTemplate redisTemplate;

    public RedisCacheAspect(CustomRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Around("@annotation(org.etsntesla.gava.utils.annotations.PreferenceRedisCache)")
    public Object afterReturningAnnotatedMethod(ProceedingJoinPoint joinPoint){
        try {
            Pair<String, PreferenceType> values = values(joinPoint);
            switch (values.getSecond()){

                case GAME: return getGame(joinPoint,values.getFirst());

                default:return joinPoint.proceed();
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @After("@annotation(org.etsntesla.gava.utils.annotations.PreferenceRedisCache)")
    public void afterVoidAnnotatedMethod(JoinPoint joinPoint){
        try {
            Pair<String, PreferenceType> values = values(joinPoint);
            switch (values.getSecond()){

                case GAME: putGame(joinPoint,values.getFirst());

                default: {}

            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }


    @After("@annotation(org.etsntesla.gava.utils.annotations.PreferenceRedisCacheDelete)")
    public void deleteGame(JoinPoint joinPoint){
        Object arg = getArg(joinPoint);
        int id;
        if (arg!=null)
            id = (Integer) arg;
        else
            throw new NullPointerException(joinPoint.getArgs().getClass().toString());
        redisTemplate.delete(Integer.toString(id));
    }




    private Object getGame(ProceedingJoinPoint joinPoint,String action) throws Throwable {
        if(action.equals(Provisions.CREATE)){
            Game game = (Game)joinPoint.proceed();
            //GameContextRepository gameContext = (GameContextRepository) game.getGameContextListener();
            //redisTemplate.opsForValue().set(Integer.toString(game.GAME_ID),gameContext);
            return game;
        }

        if(action.equals(Provisions.PULL)){
            Object arg = getArg(joinPoint);
            String name;

            if(arg!=null)
                name=(String)arg;
            else
                throw new NullPointerException(action.getClass().toString());
            Object result = redisTemplate.opsForValue().get(name);
            return result;
        }


        return joinPoint.proceed();
    }


    private void putGame(JoinPoint joinPoint,String action){
        if((action.equals(Provisions.PUT))){
            Object arg = getArg(joinPoint);
            Game game;
            if(arg instanceof Game) {
                game = (Game) arg;
                redisTemplate.opsForValue().set(Integer.toString(game.GAME_ID), game.getGameContextListener());
                return;
            } else
                throw new NullPointerException(action.getClass().toString());
        }
    }

    private Pair<String, PreferenceType> values(JoinPoint joinPoint) throws NoSuchMethodException {
            System.out.println(joinPoint.getSignature().getName());
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Method method = methodSignature.getMethod();
            PreferenceRedisCache preferenceRedisCache = method.getAnnotation(PreferenceRedisCache.class);

            return Pair.of(preferenceRedisCache.value(),preferenceRedisCache.type());
    }
    @Nullable
    private Object getArg(JoinPoint joinPoint){
        System.out.println(joinPoint.getSignature().getName());
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Parameter parameter=null;
        int index = 0;
        for(Parameter p: method.getParameters()) {
            if (Arrays.stream(p.getAnnotations())
                    .anyMatch(annotation -> annotation instanceof AppCacheArg)) {
                parameter = p;
                break;
            }
            index++;
        }
        return parameter!=null?joinPoint.getArgs()[index]:null;
    };

}
