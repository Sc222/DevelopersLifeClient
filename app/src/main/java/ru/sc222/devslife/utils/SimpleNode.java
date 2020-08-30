package ru.sc222.devslife.utils;

public class SimpleNode<T> {
    private T item;
    private SimpleNode<T> previous;
    private SimpleNode<T> next;

    public SimpleNode(T item) {
        this.item = item;
    }

    public T getItem() {
        return item;
    }

    public SimpleNode<T> getPrevious() {
        return previous;
    }

    public void setPrevious(SimpleNode<T> previous) {
        this.previous = previous;
    }

    public SimpleNode<T> getNext() {
        return next;
    }

    public void setNext(SimpleNode<T> next) {
        this.next = next;
    }
}
