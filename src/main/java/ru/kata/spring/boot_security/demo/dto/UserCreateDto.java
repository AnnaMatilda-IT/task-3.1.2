package ru.kata.spring.boot_security.demo.dto;

public class UserCreateDto {
    //DTO для создания/обновления пользователей
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private Integer age;
    private Long[] roleIds; // Массив ID ролей

    public UserCreateDto() {}

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public Long[] getRoleIds() { return roleIds; }
    public void setRoleIds(Long[] roleIds) { this.roleIds = roleIds; }
}
