package com.avaliacao.avaliacao1pw;

import jakarta.servlet.http.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
@RestController
public class Avaliacao1PwApplication {

    private final Set<String> emailsCadastrados = new HashSet<>();

    public static void main(String[] args) {
        SpringApplication.run(Avaliacao1PwApplication.class, args);
    }

    @GetMapping("/cadastro")
    public void mostrarFormulario(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String erro = request.getParameter("erro");
        response.setContentType("text/html");
        var out = response.getWriter();
        out.println("<html><body>");
        if (erro != null) {
            out.println("<p style='color:red;'>" + erro + "</p>");
        }
        out.println("<form method='post' action='/cadastro'>");
        out.println("Nome: <input type='text' name='nome' required><br>");
        out.println("Email: <input type='email' name='email' required><br>");
        out.println("Senha: <input type='password' name='senha' required><br>");
        out.println("<button type='submit'>Cadastrar</button>");
        out.println("</form>");
        out.println("</body></html>");
    }

    @PostMapping("/cadastro")
    public void processarCadastro(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nome = request.getParameter("nome");
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        if (email == null || !email.contains("@")) {
            response.sendRedirect("/cadastro?erro=Email inválido");
            return;
        }

        synchronized (emailsCadastrados) {
            if (emailsCadastrados.contains(email)) {
                response.sendRedirect("/cadastro?erro=Email já cadastrado");
                return;
            }
            emailsCadastrados.add(email);
        }

        HttpSession session = request.getSession();
        session.setAttribute("usuario", nome);

        Cookie cookie = new Cookie("SESSIONID", session.getId());
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        response.sendRedirect("/dashboard");
    }

    @GetMapping("/dashboard")
    public void dashboard(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        String usuario = session != null ? (String) session.getAttribute("usuario") : null;

        if (usuario == null) {
            response.sendRedirect("/cadastro?erro=Por favor, cadastre-se primeiro.");
            return;
        }

        response.setContentType("text/html");
        var out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>Bem-vindo, " + usuario + "!</h1>");
        out.println("</body></html>");
    }
}
