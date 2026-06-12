package com.edu.basic.service.impl;

import com.edu.basic.repositary.CommonRepository;
import com.edu.basic.service.BasicService;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;


import java.util.List;
import java.util.Map;

@NoArgsConstructor
public class BasicServiceImpl<T,E,R,ID> implements BasicService<T,E,R> {

    private CommonRepository<E,ID> commonRepository;

    //------------------------Create method ----------------------------//

    @Override
    public  ResponseEntity<R> create(T requestObject) {
        validateBeforeCreate(requestObject);
        E entity = mapDtoToEntity(requestObject); // Now returns T
        E saved = commonRepository.save(entity);
        return buildreateSuccessResponse(saved);

    }

    @Override
    public void validateBeforeCreate(T requestObject) {
        // Implementation
    }

    @Override
    public E mapDtoToEntity(T requestObject) {
        // Implementation: map R to T
        return null;
    }


    @Override
    public ResponseEntity<R> buildreateSuccessResponse(E entity) {
        // Implementation: map E to R and wrap in ResponseEntity
        return ResponseEntity.ok().build();
    }



    //------------------------GetAll method ----------------------------//

    @Override
    public void getAll() {
        buildGetAllSuccessResponse();
    }

    @Override
    public ResponseEntity<List> buildGetAllSuccessResponse() {
        //          implements Logic
        return ResponseEntity.ok().build();
    }


    //------------------------GetbyId method ----------------------------//

    @Override
    public void getById(E id) {
        E entity  = validateBeforeId(id);
        buildGetByIdSuccessResponse(entity);
    }

    @Override
    public <E>E validateBeforeId(E id){
        return null;
    }

    @Override
    public ResponseEntity<R> buildGetByIdSuccessResponse(Object entity) {
        //          implements Logic
        return ResponseEntity.ok().build();
    }


    //------------------------update method ----------------------------//


    @Override
    public void update(E id, T object) {
        validateBeforeUpdate(id);
        E updatedEntity  = validateMappedUpdate(object);
        E saved = commonRepository.save(updatedEntity);
        buildUpdateSuccessResponse(saved);
    }

    @Override
    public void validateBeforeUpdate(E id) {
        //          implements Logic
    }

    @Override
    public  E validateMappedUpdate(T object) {
        //          implements Logic
        return null;
    }

    @Override
    public ResponseEntity<R> buildUpdateSuccessResponse(Object updatedEntity) {
        //          implements Logic
        return ResponseEntity.ok().build();
    }


//------------------------delete method ----------------------------//

    @Override
    public void validateIdForDelete(E id) {
        checkExistsForDelete(id);
        delete(id);
    }

    @Override
    public void checkExistsForDelete(E id) {
        // Implementation : check before exists
    }

    @Override
    public ResponseEntity<R> delete(E id) {
        return null;
    }



    //-----------------------Patch method-----------------------------//

    @Override
    public void patch(E id, Map<String, Object> updates) {

        checkExistsById(id);
        E entity = validatePatchFields(updates);
        E updated = commonRepository.save(entity);
        buildPatchSuccessResponse(updated);
    }
    @Override
    public boolean checkExistsById(E id){
        // Implementation : check feilds if it is null
        return false;
    }

    @Override
    public E validatePatchFields(Map<String, Object> updates) {
        // Implementation : check feilds if it is null
        return null;
    }

    @Override
    public ResponseEntity<R> buildPatchSuccessResponse(E entity) {

        // map entity to response dto
        return ResponseEntity.ok(null);
    }


}
