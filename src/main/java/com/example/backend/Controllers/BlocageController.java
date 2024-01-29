package com.example.backend.Controllers;

import com.example.backend.entities.Blocage;
import com.example.backend.entities.BlocageRequest;
import com.example.backend.services.BlocageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/api/blocages")
public class BlocageController {

    @Autowired
    private BlocageRepository blocageRepository;

    @PostMapping("/add")
    public String addBlocages(@RequestBody BlocageRequest blocageRequest) {
        String msisdnData = blocageRequest.getMsisdn();
        String type = blocageRequest.getType();

        List<String> msisdns = Arrays.asList(msisdnData.split("[,\\s\\n]+"));

        for (String msisdn : msisdns) {
            // Validate the length of MSISDN and type (you may want to add more validation)
            if (msisdn.length() == 8) {
                Blocage blocage = new Blocage();
                blocage.setMsisdn(msisdn);
                blocage.setType(type);

                blocageRepository.save(blocage);
            }
        }

        return "Blocage(s) added successfully";
    }

    @GetMapping
    public List<Blocage> getAllBlocages() {
        return blocageRepository.findAll();
    }


}
