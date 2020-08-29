package ru.sc222.devslife.core;

public class SimpleNode<T>{
    T item;
    SimpleNode<T> previous;

    SimpleNode<T> next;

    public SimpleNode(T item) {
        this.item = item;
    }

    public T getItem() {
        return item;
    }

    public SimpleNode<T> getPrevious() {
        return previous;
    }

    public SimpleNode<T> getNext() {
        return next;
    }
}
