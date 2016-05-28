package com.github.gressy.entities.model;

/**
 * Interface for all the entities that will use the Panel.
 *
 * To be able of using the panel with them, entities need to have a id of type long,
 * as it will be part of the URL for the CRUD endpoints. This also allows us to make
 * a simple generic Dao for all of them.
 *
 * @see EntityDao
 */
public interface Entity {

    long getId();

    void setId(long id);

}
