package com.example.backend.Controllers;

import com.example.backend.dao.SubmoduleReportingRepository;
import com.example.backend.entities.Submodule_Reporting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/submodulereporting")
public class SubmoduleReportingController {


    @Autowired
    private SubmoduleReportingRepository submoduleReportingRepository;


    @PostMapping("/add")
    public ResponseEntity<Submodule_Reporting> assign(@RequestBody Submodule_Reporting submodule_reporting) {
        try {
            Submodule_Reporting savedSubmoduleReporting = submoduleReportingRepository.save(submodule_reporting);
            return new ResponseEntity<>(savedSubmoduleReporting, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
