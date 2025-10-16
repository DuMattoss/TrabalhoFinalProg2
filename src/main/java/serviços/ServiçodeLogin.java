package serviços;

import dao.UsuarioDao;
import dao.OperadorDao;
import org.example.carrosuenp.Usuario;
import org.example.carrosuenp.Operador;

public class ServiçodeLogin {

    private final UsuarioDao usuarioDao = new UsuarioDao();
    private final OperadorDao operadorDao = new OperadorDao();


    public boolean autenticarUsuario(String loginDigitado, String senhaDigitada) {
        System.out.println("[Login] Tentando login de USUÁRIO = '" + loginDigitado + "'");
        Usuario usuario = usuarioDao.buscarPorLogin(loginDigitado);

        if (usuario == null) {
            System.out.println("[Login] Usuário NÃO encontrado para login='" + loginDigitado + "'");
            return false;
        }

        String senhaBanco = (usuario.getSenha() == null) ? "" : usuario.getSenha().trim();
        String senhaDigitadaTrim = senhaDigitada == null ? "" : senhaDigitada.trim();

        boolean ok = senhaBanco.equals(senhaDigitadaTrim);
        System.out.println("[Login] Resultado = " + ok);
        return ok;
    }


    public Operador autenticarOperador(String loginDigitado, String senhaDigitada, String codigoDigitado) {
        System.out.println("[Login] Tentando login de OPERADOR = '" + loginDigitado + "'");

        Operador op = operadorDao.buscarPorLogin(loginDigitado);
        if (op == null) {
            System.out.println("[Login] Operador NÃO encontrado para login='" + loginDigitado + "'");
            return null;
        }

        String senhaBanco  = op.getSenha()  == null ? "" : op.getSenha().trim();
        String codigoBanco = op.getCodigo() == null ? "" : op.getCodigo().trim();
        String senhaIn     = senhaDigitada  == null ? "" : senhaDigitada.trim();
        String codigoIn    = codigoDigitado == null ? "" : codigoDigitado.trim();

        boolean ok = senhaBanco.equals(senhaIn) && codigoBanco.equals(codigoIn);
        System.out.println("[Login] Resultado = " + ok);
        return ok ? op : null;
    }
}
