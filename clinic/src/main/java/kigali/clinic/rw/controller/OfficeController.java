package kigali.clinic.rw.controller;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinic.rw.domain.Office;
import kigali.clinic.rw.service.OfficeService;

@RestController
@RequestMapping(value = {"/api/office", "/api/offices"})
public class OfficeController {

    @Autowired
    private OfficeService offServe;

    @PostMapping(value = "/save")
    public ResponseEntity<?> saveOffice(@RequestBody Office office) {
        String message = offServe.saveOffice(office);
        return new ResponseEntity<>(message, statusOf(message));
    }

    @GetMapping(value = "/all")
    public ResponseEntity<?> getAllOffices() {
        return new ResponseEntity<>(offServe.getAllOffices(), HttpStatus.OK);
    }

    @GetMapping(value = "/busiest")
    public ResponseEntity<?> getBusiest() {
        Optional<Object[]> row = offServe.getBusiestOffice();
        if (row.isPresent()) {
            return new ResponseEntity<>(row.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("No appointments yet", HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getOfficeById(@PathVariable("id") UUID id) {
        Optional<Office> office = offServe.getOfficeById(id);
        if (office.isPresent()) {
            return new ResponseEntity<>(office.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("Office not found", HttpStatus.NOT_FOUND);
    }

    @PutMapping(value = "/update/{id}")
    public ResponseEntity<?> updateOffice(@PathVariable("id") UUID id, @RequestBody Office office) {
        String message = offServe.updateOffice(id, office);
        return new ResponseEntity<>(message, statusOf(message));
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deleteOffice(@PathVariable("id") UUID id) {
        String message = offServe.deleteOffice(id);
        return new ResponseEntity<>(message, statusOf(message));
    }

    private HttpStatus statusOf(String message) {
        if (message.endsWith("successfully")) {
            return HttpStatus.OK;
        }
        if (message.endsWith("not found")) {
            return HttpStatus.NOT_FOUND;
        }
        return HttpStatus.CONFLICT;
    }
    @DeleteMapping(value = "/number/{officeNumber}")
    public ResponseEntity<?> deleteOfficeByNumber(@PathVariable("officeNumber") int officeNumber) {
    String message = offServe.deleteOfficeByNumber(officeNumber);
    return new ResponseEntity<>(message, statusOf(message));
}

}