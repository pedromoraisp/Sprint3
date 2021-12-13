package br.com.sprint3.sprint3.controllers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.sprint3.sprint3.controllers.dto.EstadoDto;
import br.com.sprint3.sprint3.controllers.form.EstadoForm;
import br.com.sprint3.sprint3.estado.Estado;
import br.com.sprint3.sprint3.repositories.EstadoRepository;

@RestController
@RequestMapping("/api")
public class EstadoController {
	
	@Autowired
	private EstadoRepository estadoRepository;
	
	@GetMapping("/states")
	public ResponseEntity<?> listaComFiltro(@RequestParam String filtro) {
		
		List<Estado> estados = null;
		if (filtro.equalsIgnoreCase("Regiao")) {
			estados = estadoRepository.findAll(Sort.by("regiao"));
		}
		else if (filtro.equalsIgnoreCase("Populacao")){
			estados = estadoRepository.findAll(Sort.by("populacao").descending());
		}
		else if (filtro.equalsIgnoreCase("Area")) {
			estados = estadoRepository.findAll(Sort.by("area").descending());
		} else {
			return ResponseEntity.badRequest().build();
		}
		
		List<EstadoDto> estadosDto = new ArrayList<EstadoDto>();
		estados.forEach(x->{
			estadosDto.add(new EstadoDto(x));
		});
		return ResponseEntity.ok(estadosDto);
	}
		
	@GetMapping("/states/{id}")
	public ResponseEntity<EstadoDto> buscaPorId(@PathVariable int id) {
		Optional<Estado> estados = estadoRepository.findById(id);
		if (estados.isPresent()) {
			return ResponseEntity.ok(new EstadoDto(estados.get()));
		}
		return ResponseEntity.notFound().build();
	}
	
	@PostMapping("/states")
	@Transactional
	public ResponseEntity<EstadoDto> cadastrar(@RequestBody @Validated EstadoForm form,
			UriComponentsBuilder uriBuilder) {
		
			Estado estado = new Estado(form.getNome(), form.getRegiao(), form.getPopulacao(), 
					form.getCapital(), form.getArea());
			System.out.println("Estado nome: " + estado.getNome());
			estadoRepository.save(estado); 
			URI uri = uriBuilder.path("/api/states/{id}").buildAndExpand(estado.getId()).toUri();
			return ResponseEntity.created(uri).body(new EstadoDto(estado));
	}
	
	@PutMapping("/states/{id}")
	@Transactional
	public ResponseEntity<EstadoDto> alterar(@PathVariable int id, @RequestBody @Validated EstadoForm form) {
		Optional<Estado> optional = estadoRepository.findById(id);
		if (optional.isPresent()) {
			return ResponseEntity.ok(new EstadoDto(form.atualizar(id, estadoRepository)));
		}
		return ResponseEntity.notFound().build();
	}

	@DeleteMapping("/states/{id}")
	@Transactional
	public ResponseEntity<?> remover(@PathVariable int id) {
		Optional<Estado> optional = estadoRepository.findById(id);
		if (optional.isPresent()) {
			estadoRepository.deleteById(id);
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.notFound().build();
	}
	
}
