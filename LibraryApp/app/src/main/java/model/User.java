package model;

import java.util.List;

public class User {
    private String email;
    private String userName;
    private String password;
    private String phoneNo;
    private String userId;
    private List <Book> myLibrary;


    public User(String email, String userName, String password, String phoneNo, String userId,  List<Book> myLibrary) {
        this.email = email;
        this.userName = userName;
        this.password = password;
        this.phoneNo = phoneNo;
        this.userId = userId;
        this.myLibrary = myLibrary;

    }



    public User(){

    }

    public List<Book> getMyLibrary() {
        return myLibrary;
    }

    public void setMyLibrary(List<Book> myLibrary) {
        this.myLibrary = myLibrary;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString(){
        return userId + userName + password + phoneNo + email +myLibrary;
    }
}
