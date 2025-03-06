package com.ivnrdev.connectodo.Mapper;

public interface Mapper<E, req, res> {
    E toEntity(req request);

    res toRes(E entity);
}
