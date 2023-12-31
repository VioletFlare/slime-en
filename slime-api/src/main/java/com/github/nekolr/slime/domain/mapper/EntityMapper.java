package com.github.nekolr.slime.domain.mapper;


import java.util.List;

/**
 * Interface to map a physical device
 *
 * @param <E> entity Type
 * @param <D> dto Type
 */
public interface EntityMapper<E, D> {

    /**
     * Email entity Image map area dto
     *
     * @param entity Type
     * @return
     */
    D toDto(E entity);

    /**
     * Email dto Image map area entity
     *
     * @param dto
     * @return
     */
    E toEntity(D dto);

    /**
     * entity Combining Prohibited Characters dto Conversation
     *
     * @param entityList
     * @return
     */
    List<D> toDto(List<E> entityList);

    /**
     * dto Map to Assistant entity Conversation
     *
     * @param dtoList
     * @return
     */
    List<E> toEntity(List<D> dtoList);
}
