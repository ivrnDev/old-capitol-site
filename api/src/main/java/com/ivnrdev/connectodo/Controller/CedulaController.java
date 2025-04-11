package com.ivnrdev.connectodo.Controller;

import com.ivnrdev.connectodo.DTO.Cedula.CedulaRequestDTO;
import com.ivnrdev.connectodo.DTO.Cedula.CedulaResponseDTO;
import com.ivnrdev.connectodo.Domain.Cedula;
import com.ivnrdev.connectodo.Mapper.CedulaMapper;
import com.ivnrdev.connectodo.Response.BaseResponse;
import com.ivnrdev.connectodo.Service.CedulaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cedula")
@RequiredArgsConstructor
public class CedulaController {
    private final CedulaService cedulaService;
    private final CedulaMapper cedulaMapper;

    @PostMapping
    public ResponseEntity<BaseResponse<CedulaResponseDTO>> saveCedula(@RequestBody CedulaRequestDTO cedula) {
        Cedula savedCedula = cedulaService.saveCedula(cedulaMapper.toEntity(cedula));
        CedulaResponseDTO cedulaResponseDTO = cedulaMapper.toRes(savedCedula);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                BaseResponse.success(cedulaResponseDTO, "Cedula saved successfully")
        );
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<CedulaResponseDTO>>> getAllCedulas() {
        List<CedulaResponseDTO> cedulas = cedulaService.getAllCedulas().stream().map(data -> cedulaMapper.toRes(data)).collect(Collectors.toList());
        return ResponseEntity.ok(
                BaseResponse.success(cedulas, "Cedulas fetched successfully")
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<CedulaResponseDTO>> getCedulaById(@PathVariable Long id) {
        Cedula cedula = cedulaService.getCedulaById(id);
        CedulaResponseDTO cedulaResponseDTO = cedulaMapper.toRes(cedula);
        return ResponseEntity.ok(
                BaseResponse.success(cedulaResponseDTO, "Cedula fetched successfully")
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BaseResponse<CedulaResponseDTO>> updateCedula(@PathVariable Long id, @RequestBody CedulaRequestDTO cedula) {
        Cedula updatedCedula = cedulaService.updateCedulaById(id, cedulaMapper.toEntity(cedula));
        CedulaResponseDTO cedulaResponseDTO = cedulaMapper.toRes(updatedCedula);
        return ResponseEntity.ok(
                BaseResponse.success(cedulaResponseDTO, "Cedula updated successfully")
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<CedulaResponseDTO>> deleteCedula(@PathVariable Long id) {
        Cedula deletedCedula = cedulaService.deleteCedulaById(id);
        CedulaResponseDTO cedulaResponseDTO = cedulaMapper.toRes(deletedCedula);
        return ResponseEntity.ok(
                BaseResponse.success(cedulaResponseDTO, "Cedula deleted successfully")
        );
    }
}
