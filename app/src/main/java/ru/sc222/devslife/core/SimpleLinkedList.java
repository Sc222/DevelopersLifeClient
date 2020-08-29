package ru.sc222.devslife.core;

public class SimpleLinkedList<T> {

    private SimpleNode<T> first, last = null;

    public void addLast(T item) {
        SimpleNode<T> newNode = new SimpleNode<>(item);
        if (first == null) {
            first = last = newNode;
            first.setPrevious(null);
            last.setNext(null);
        } else {
            newNode.setNext(null);
            last.setNext(newNode);
            newNode.setPrevious(last);
            last = newNode;
        }
    }

    public SimpleNode<T> getLast() {
        return last;
    }
}