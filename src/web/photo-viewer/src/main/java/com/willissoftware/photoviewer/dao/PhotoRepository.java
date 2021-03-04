package com.willissoftware.photoviewer.dao;

import com.willissoftware.photoviewer.common.domain.entity.PhotoEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends CrudRepository<PhotoEntity, Long> {

    public PhotoEntity findFirstByFilenameEquals(String filename);
}
