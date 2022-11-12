package com.example.alquilab.model;

public class Casa {
    private String id;
    private String description ="";
    private String address="";
    //private Image imagen;

    public String getId() {
        return id;
    }
    public String getDescription() {
        return description;
    }
    public String getAddress() {
        return address;
    }

    /*public Image getImagen() {
        return imagen;
    }
*/
    public void setId(String id) {
        this.id=id ;
    }
    public void setDescription(String description) {
        this.description=description;
    };
    public void setAddress(String address) {
        this.address=address;
    };
  /*  public void setImagen(Image img) {
        this.imagen=img;
    };*/

}


