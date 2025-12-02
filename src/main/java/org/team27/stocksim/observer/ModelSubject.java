package org.team27.stocksim.observer;

public interface ModelSubject {
    void addObserver(ModelObserver obs);
    void removeObserver(ModelObserver obs);
}
