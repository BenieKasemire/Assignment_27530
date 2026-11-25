package kigali.clinic.rw.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinic.rw.domain.Office;
import kigali.clinic.rw.repository.OfficeRepository;

@Service
public class OfficeService {

    @Autowired
    private OfficeRepository offRepo;

    public String saveOffice(Office office) {
        if (offRepo.findByOfficeNumber(office.getOfficeNumber()).isPresent()) {
            return "Office number already exists";
        }
        office.setId(null);
        offRepo.save(office);
        return "Office is saved successfully";
    }

    public List<Office> getAllOffices() {
        return offRepo.findAll();
    }

    public Optional<Office> getOfficeById(UUID id) {
        return offRepo.findById(id);
    }

    public String updateOffice(UUID id, Office newOffice) {
        Optional<Office> found = offRepo.findById(id);
        if (found.isEmpty()) {
            return "Office not found";
        }

        Optional<Office> sameNumber = offRepo.findByOfficeNumber(newOffice.getOfficeNumber());
        if (sameNumber.isPresent() && !sameNumber.get().getId().equals(id)) {
            return "Office number already exists";
        }

        Office office = found.get();
        office.setName(newOffice.getName());
        office.setOfficeNumber(newOffice.getOfficeNumber());
        offRepo.save(office);
        return "Office is updated successfully";
    }

    @Transactional
    public String deleteOffice(UUID id) {
        Optional<Office> found = offRepo.findById(id);
        if (found.isEmpty()) {
            return "Office not found";
        }
        if (found.get().getDoctor() != null) {
            return "Office is assigned to a doctor, delete the doctor first";
        }
        offRepo.deleteById(id);
        return "Office is deleted successfully";
    }
    public Optional<Object[]> getBusiestOffice() {
        List<Object[]> rows = offRepo.findBusiestOffice(PageRequest.of(0, 1));
        if (rows.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(rows.get(0));
    }

    public String deleteOfficeByNumber(int officeNumber) {
        Optional<Office> found = offRepo.findByOfficeNumber(officeNumber);
        if (found.isEmpty()) return "Office not found";
        return deleteOffice(found.get().getId());
    }
}
