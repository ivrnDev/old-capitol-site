package com.ivnrdev.connectodo.Controller;

import com.ivnrdev.connectodo.DTO.Resident.ResidentRequestDTO;
import com.ivnrdev.connectodo.DTO.Resident.ResidentResponseDTO;
import com.ivnrdev.connectodo.Domain.Resident;
import com.ivnrdev.connectodo.Mapper.ResidentMapper;
import com.ivnrdev.connectodo.Response.BaseResponse;
import com.ivnrdev.connectodo.Service.ResidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/resident")
@RequiredArgsConstructor
public class ResidentController {
    private final ResidentService residentService;
    private final ResidentMapper residentMapper;

    @PostMapping
    public ResponseEntity<BaseResponse<ResidentResponseDTO>> saveResident(@RequestBody ResidentRequestDTO resident) {
        Resident savedResident = residentService.saveResident(residentMapper.toEntity(resident));
        ResidentResponseDTO residentResponseDTO = residentMapper.toRes(savedResident);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                BaseResponse.success(residentResponseDTO, "Resident saved successfully")
        );
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<ResidentResponseDTO>>> getAllResidents() {
        List<ResidentResponseDTO> residents = residentService.getAllResidents().stream().map(data -> residentMapper.toRes(data)).collect(Collectors.toList());
        return ResponseEntity.ok(
                BaseResponse.success(residents, "Residents fetched successfully")
        );
    }
}
