package com.project.bank1.mapper;

import java.util.List;
import java.util.stream.Collectors;

public interface ModelToDtoMapper<Model, Dto> {

    Model dtoToModel(Dto dto);

    Dto modelToDto(Model model);

    default List<Model> dtoToModel(List<Dto> dtos) {
        List<Model> models = dtos.stream().map(dto -> dtoToModel(dto)).collect(Collectors.toList());
        return models;
    }

    default List<Dto> modelToDto(List<Model> models) {
        List<Dto> dtos = models.stream().map(model -> modelToDto(model)).collect(Collectors.toList());
        return dtos;
    }
}
