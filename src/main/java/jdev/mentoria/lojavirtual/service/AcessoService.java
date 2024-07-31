package jdev.mentoria.lojavirtual.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jdev.mentoria.lojavirtual.model.Acesso;
import jdev.mentoria.lojavirtual.repository.AcessoRepository;
	
@Service
public class AcessoService {

	@Autowired
	private AcessoRepository acessoRepository;
	
	/*Qualuer tipo de validacao aintes de  salvar*/
	public Acesso  save(Acesso acesso) {
	
	    if (acesso.getDescricao() == null) {
	        throw new IllegalArgumentException("Descrição não pode ser nula");
	    }
	    	return acessoRepository.save(acesso);
		}
		
	}

