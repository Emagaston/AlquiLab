package com.example.alquilab;

public class User {

    public String nombre, email, rol, number;

    public User(){

    }

    public String getRol() {
        return rol;
    }

    public User(String nombre, String email, String rol, String number){
        this.nombre = nombre;
        this.email = email;
        this.rol = rol;
        this.number = number;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
