package ru.sc222.devslife.core;

public class SimpleLinkedList<T> {

    SimpleNode<T> first, last = null;

    //add a node to the list
    public void addLast(T item) {
        //Create a new node
        SimpleNode<T> newNode = new SimpleNode<>(item);
        //if list is empty, head and tail points to newNode
        if (first == null) {
            first = last = newNode;
            first.previous = null;
            last.next = null;
        } else {
            newNode.next = null;
            last.next = newNode;
            newNode.previous = last;
            last = newNode;
        }
    }

    public SimpleNode<T> getLast() {
        return last;
    }
}