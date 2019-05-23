package org.aprestos.labs.apis.springboot2.statemanager;

import java.util.Map;
import java.util.Optional;

public interface StateManager<I,O,S> {

	void notify(O state) throws StateManagerException;

	Optional<S> getStatus(I id) throws StateManagerException;

	Map<Long,S> getAllStatus() throws StateManagerException;

	Optional<O> getState(I id) throws StateManagerException;

}
