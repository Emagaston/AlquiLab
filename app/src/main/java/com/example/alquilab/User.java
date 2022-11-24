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
}
