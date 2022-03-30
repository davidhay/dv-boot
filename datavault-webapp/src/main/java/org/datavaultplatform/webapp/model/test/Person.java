package org.datavaultplatform.webapp.model.test;

public class Person {

  private String first;
  private String last;

  public Person() {
  }

  public Person(String first, String last){
    this.first = first;
    this.last = last;
  }

  public String getFirst(){
    return first;
  }

  public String getLast(){
    return last;
  }

  public void setFirst(String first) {
    this.first = first;
  }

  public void setLast(String last) {
    this.last = last;
  }
}
