package org.openmrs.module.cfl.api.mapper;

import org.openmrs.BaseOpenmrsData;

import java.util.List;

public interface Mapper<T, R extends BaseOpenmrsData> {

    T toDto(R dao);

    R fromDto(T dto);

    List<T> toDtos(List<R> daos);

    List<R> fromDtos(List<T> dtos);
}
