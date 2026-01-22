package br.edu.ifs.academico.config;

public class SecurityEndpoints {

    private SecurityEndpoints() {
    }

    public static final String[] ADMIN_WRITE = {
            "/promocao/**",
            "/programa/**",
            "/notificacao/**"
    };
}
