package br.com.javace.controller;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.util.test.MockResult;
import br.com.caelum.vraptor.util.test.MockValidator;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.SimpleMessage;
import br.com.caelum.vraptor.validator.ValidationException;
import br.com.javace.model.Usuario;
import br.com.javace.service.UsuarioService;

public class UsuarioControllerTest {

	@Mock private UsuarioService service;
	
	private UsuarioController controller;
	
	private MockResult result;
	private MockValidator validator;
	
	@Before
	public void setUp(){
		MockitoAnnotations.initMocks(this);
		result = new MockResult();
		validator = new MockValidator();
		controller = new UsuarioController(result, service, validator);
	}
	
	@Test
	public void deveListarTodosOsUsuarios(){
		when(service.listar()).thenReturn(Arrays.asList(usuarioMatheus()));
		controller.listar();
		assertEquals(Arrays.asList(usuarioMatheus()), result.included().get("usuarios"));
	}

	private Usuario usuarioMatheus() {
		Usuario usuario = new Usuario();
		usuario.setLogin("matheus");
		usuario.setSenha("12345");
		return usuario;
	}
	
	@Test
	public void deveAdicionarUmUsuario(){
		controller.adiciona(usuarioMatheus());
		assertEquals("Usuario adicionado com sucesso!", result.included().get("sucesso"));
	}
	
	@Test
	public void deveriaChamarAPaginaDeIncluir(){
		controller.paginaDeIncluir();
	}
	
	@Test
	public void deveriaLancarErroDeValidacaoQuandoLoginNaoInformado(){
		Usuario user = usuarioMatheus();
		user.setLogin(null);
		String campo = "usuario";
		String mensagem = "Login não informado!";
		valida(user, campo, mensagem);
	}
	
	@Test
	public void deveriaLancarErroDeValidacaoQuandoPasswordNaoInformado(){
		Usuario user = usuarioMatheus();
		user.setSenha(null);
		String campo = "senha";
		String mensagem = "Senha não informada!";
		valida(user, campo, mensagem);
	}

	private void valida(Usuario user, String campo, String mensagem) {
		try {
			controller.adiciona(user);
			fail("Deveria ocorrer um erro de validação");
		} catch (ValidationException e) {
			List<Message> errors = e.getErrors();
			assertTrue(errors.contains(new SimpleMessage(campo, mensagem)));
			assertEquals(1, errors.size());
		}
	}
	
}

