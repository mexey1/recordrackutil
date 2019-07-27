package com.recordrack.interfaces;

/**
 * Implement this interface to listen for results of a query
 * @param <T> Type of the result returned from the query
 */
public interface QueryResultListener<T>
{
    public void doAction(T result);
}
