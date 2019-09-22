package com.dragosenic.common;

/**
 *  Will be used in case when json body is malformed:
 *      Body could not be parsed as JSON or,
 *      Json cannot deserialize to target type, or
 *      Data field are not given in correct format
 */
public class InvalidPostDataException extends Exception {
    public InvalidPostDataException(String errorMessage) {
        super(errorMessage);
    }
}