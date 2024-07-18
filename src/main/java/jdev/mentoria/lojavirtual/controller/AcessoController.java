package jdev.mentoria.lojavirtual.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.websocket.server.PathParam;
import jdev.mentoria.lojavirtual.model.Acesso;
import jdev.mentoria.lojavirtual.repository.AcessoRepository;
import jdev.mentoria.lojavirtual.service.AcessoService;

@Controller
@RestController
//@RequestMapping("/api/acesso")
public class AcessoController {
	
	@Autowired
	private AcessoRepository acessoRepository;
	@Autowired
	private AcessoService acessoService;
	
	@ResponseBody /*pode dar retorno da api*/
	@PostMapping(value = "/salvarAcesso")/*Mapeando a url para receber JSON*/
	public ResponseEntity<Acesso> salvarAcesso(@RequestBody Acesso acesso) { /*recebe json e converte para objeto*/
		
		Acesso acessoSalvo = acessoService.save(acesso);
		
		return new ResponseEntity<Acesso>(acessoSalvo,HttpStatus.OK);
	}

	@ResponseBody /*pode dar retorno da api*/
	@PostMapping(value = "/deleteAcesso")/*Mapeando a url para receber JSON*/
	public ResponseEntity<?> deleteAcesso(@RequestBody Acesso acesso) { /*recebe json e converte para objeto*/
		
		acessoRepository.deleteById(acesso.getId());
		
		return new ResponseEntity("Acesso Removido",HttpStatus.OK);
	}
	
	@ResponseBody 
	@DeleteMapping(value = "/deleteAcessoPorId/{id}")/*Mapeando a url para receber JSON*/
	public ResponseEntity<?> deleteAcessoPorId(@PathVariable("id") Long id) { /*recebe json e converte para objeto*/
		
		acessoRepository.deleteById(id);
		
		return new ResponseEntity("Acesso Removido",HttpStatus.OK);
	}
	
	@ResponseBody 
	@GetMapping(value = "/obterAcesso/{id}")/*Mapeando a url para receber JSON*/
	public ResponseEntity<Acesso> obterAcesso(@PathVariable("id") Long id) { /*recebe json e converte para objeto*/
		
	 Acesso acesso = acessoRepository.findById(id).get();
		
		return new ResponseEntity<Acesso>(acesso,HttpStatus.OK);
	}
	
	@ResponseBody 
	@GetMapping(value = "/buscarPorDesc/{desc}")/*Mapeando a url para receber JSON*/
	public ResponseEntity<List<Acesso>> buscarPorDesc(@PathVariable("desc") String desc) { /*recebe json e converte para objeto*/
		
	 List<Acesso> acesso = acessoRepository.buscarAcessoDesc(desc);
		
		return new ResponseEntity<List<Acesso>>(acesso,HttpStatus.OK);
	}
}
