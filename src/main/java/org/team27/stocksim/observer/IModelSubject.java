package org.team27.stocksim.observer;

public interface IModelSubject {
    void addObserver(IModelObserver obs);

    void removeObserver(IModelObserver obs);
}
