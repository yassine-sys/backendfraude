package com.example.backend.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity(name = "sub_module")
@Table(schema = "fraudemangement")
@JsonIdentityInfo(scope = SubModule.class,resolver = EntityIdResolver.class,generator = ObjectIdGenerators.PropertyGenerator.class,property = "id")
public class SubModule  implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "submodulename")
    @NonNull
    private String subModuleName;

    @Column(name = "path")
    @NonNull
    private String path;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id")
 //   @JsonBackReference
    private Module Module;

    @ManyToMany(mappedBy = "liste_submodule", cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Group> group;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NonNull
    public String getSubModuleName() {
        return subModuleName;
    }

    public void setSubModuleName(@NonNull String subModuleName) {
        this.subModuleName = subModuleName;
    }

    @NonNull
    public String getPath() {
        return path;
    }

    public void setPath(@NonNull String path) {
        this.path = path;
    }

    public com.example.backend.entities.Module getModule() {
        return Module;
    }

    public void setModule(com.example.backend.entities.Module module) {
        Module = module;
    }

    public List<Group> getGroup() {
        return group;
    }

    public void setGroup(List<Group> group) {
        this.group = group;
    }

    public SubModule(Long id, @NonNull String subModuleName, @NonNull String path, com.example.backend.entities.Module module, List<Group> group) {
        this.id = id;
        this.subModuleName = subModuleName;
        this.path = path;
        Module = module;
        this.group = group;
    }

    public SubModule() {
    }
}
