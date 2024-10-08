package jdev.mentoria.lojavirtual.model;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "usuario")
@SequenceGenerator(name ="seq_usuario", sequenceName= "seq_usuario", allocationSize = 1, initialValue = 1)
public class Usuario implements UserDetails {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "seq_usuario")
	private Long id;
	
	@Column(nullable = false)
	private String login;
	
	@Column(nullable = false)
	private String senha;
	
	@Temporal(TemporalType.DATE)
	private String dataAtualSenha;
	
	
	@OneToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "usuario_acesso", 
				uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id","acesso_id"},
				name = "unique_acesso_user"), 
				
	joinColumns = @JoinColumn(name= "usuario_id",referencedColumnName = "id", table = "usuario",
				unique = false, foreignKey = @ForeignKey(name="usuario_fk",value = ConstraintMode.CONSTRAINT)),
	
	inverseJoinColumns = @JoinColumn(name="acesso_id",
				unique = false,referencedColumnName = "id", table = "acesso",
				foreignKey = @ForeignKey(name = "acesso_fk", value = ConstraintMode.CONSTRAINT)))
	private List<Acesso> acessos;
	
	/*Autoridades = saos acesso, ou senha ROLE_ADMIN, ROLE_SECRETARIO, ROLE_FINANCEIRO*/
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return this.acessos;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return this.senha;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return this.login;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	
	@Override
	public boolean isAccountNonLocked()  {
		return true;
	}
	
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
  
	public boolean isEnable() {
		return true;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getDataAtualSenha() {
		return dataAtualSenha;
	}

	public void setDataAtualSenha(String dataAtualSenha) {
		this.dataAtualSenha = dataAtualSenha;
	}

	public List<Acesso> getAcessos() {
		return acessos;
	}

	public void setAcessos(List<Acesso> acessos) {
		this.acessos = acessos;
	}
	
	


}
