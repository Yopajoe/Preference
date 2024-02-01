package org.etsntesla.gava.repositories;

import org.etsntesla.gava.models.GameContext;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GameContextRepository extends CrudRepository<GameContext, String> {

}
