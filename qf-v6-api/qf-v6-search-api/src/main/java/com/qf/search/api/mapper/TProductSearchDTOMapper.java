package com.qf.search.api.mapper;

import com.qf.search.api.entity.TProductSearchDTO;

import java.util.List;


public interface TProductSearchDTOMapper {

    List<TProductSearchDTO> selectAll();

    TProductSearchDTO selectById(Long id);
}
