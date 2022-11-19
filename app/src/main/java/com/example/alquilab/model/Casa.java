package com.example.alquilab.model;

public class Casa {
    private String id;
    private String nombre="";
    private String estado="Disponible";
    private String descripcion ="";
    private String direccion="";
    private String barrio="";
    private String habitaciones="";
    private String precio="";
    private String idUser="";
    private String urlFoto;
    private String latitud="";
    private String longitud="";

    /*public Casa(){
        setId("");
        setNombre("");
        setEstado("");
        setDescripcion("");
        setDireccion("");
        setBarrio("");
        setHabitaciones("");
        setPrecio("");
        setIdUser("");
        setUrlFoto("");
        setLatitud("");
        setLongitud("");
    }*/
    public String getId() {return id;    }
    public String getNombre() {return nombre;    }
    public String getEstado() {return estado;    }
    public String getDescripcion() {return descripcion;    }
    public String getDireccion() {return direccion;    }
    public String getBarrio() {return barrio;    }
    public String getHabitaciones() {return habitaciones;    }
    public String getPrecio() {return precio;    }
    public String getIdUser() {return idUser;    }
    public String getUrlFoto() {return urlFoto;}
    public String getLatitud() {return latitud;}
    public String getLongitud() {return longitud;}

    public void setId(String id) {this.id=id;};
    public void setNombre(String nombre) {this.nombre=nombre;};
    public void setEstado(String estado) {this.estado=estado;};
    public void setDescripcion(String descripcion) {this.descripcion=descripcion;};
    public void setDireccion(String direccion) {this.direccion=direccion;};
    public void setBarrio(String barrio) {this.barrio=barrio;};
    public void setHabitaciones(String habitaciones) {this.habitaciones=habitaciones;};
    public void setPrecio(String precio) {this.precio=precio;};
    public void setIdUser(String idUser) {this.idUser=idUser;};
    public void setUrlFoto(String urlFoto) {this.urlFoto=urlFoto;};
    public void setLatitud(String latitud) {this.latitud =latitud;};
    public void setLongitud(String longitud) {this.longitud = longitud;};
}




