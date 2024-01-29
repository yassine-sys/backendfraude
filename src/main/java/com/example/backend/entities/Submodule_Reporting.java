package com.example.backend.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "Submodule_Reporting")
@Table(schema = "fraudemangement")
public class Submodule_Reporting implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer Submodule_id;

    private Integer Rep_id;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSubmodule_id() {
        return Submodule_id;
    }

    public void setSubmodule_id(Integer submodule_id) {
        Submodule_id = submodule_id;
    }

    public Integer getRep_id() {
        return Rep_id;
    }

    public void setRep_id(Integer rep_id) {
        Rep_id = rep_id;
    }
}
