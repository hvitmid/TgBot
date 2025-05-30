package org.example;

public interface StateSession {

    enum State {INIT, ACTION, CHECK, END, ERORR};
    public Question action();
    public boolean check (String answer);
    public String end ();
    public State getState();

}