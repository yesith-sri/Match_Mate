package com.edu.basic.service;

import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface BasicService<T, E, R> {

    //------------------------Create method ----------------------------//

    default  ResponseEntity<R> create(T requestObject){return ResponseEntity.ok().build();}
    default void validateBeforeCreate(T requestObject){}
    default E mapDtoToEntity(T requestObject){ return null; }
    default ResponseEntity<R> buildreateSuccessResponse(E entity){ return ResponseEntity.ok().build();}


    //------------------------GetAll method ----------------------------//

    default void getAll(){}
    default ResponseEntity<List> buildGetAllSuccessResponse(){ return ResponseEntity.ok().build();}

    //------------------------GetbyId method ----------------------------//

    default void getById(E id){}
    default <E>E validateBeforeId(E id){ return id;}
    default ResponseEntity<R> buildGetByIdSuccessResponse(T entity){ return ResponseEntity.ok().build();}


    //------------------------Update method ----------------------------//

    default void update(E id, T object){}
    default void validateBeforeUpdate(E id ){}
    default E validateMappedUpdate(T object){ return null;}
    default ResponseEntity<R> buildUpdateSuccessResponse(T updatedEntity){ return ResponseEntity.ok().build();}

    //------------------------delete method ----------------------------//

    default void validateIdForDelete(E id){}
    default void checkExistsForDelete(E id){}
    default ResponseEntity<R> delete(E id){ return ResponseEntity.ok(null);}


    //-----------------------Patch method-----------------------------//

    default void patch(E id, Map<String, Object> updates){}
    default boolean checkExistsById(E id){ return false;}
    default E validatePatchFields(Map<String, Object> updates){ return null;}
    default ResponseEntity<R> buildPatchSuccessResponse(E entity) {return ResponseEntity.ok(null);}

}
