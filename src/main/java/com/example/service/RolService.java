package com.example.service;

import com.example.model.Rol;
import com.example.repository.RolRepository;
import com.example.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RolService {

    private final RolRepository repository;

    public List<Rol> listar() {
        return repository.findAll();
    }
}