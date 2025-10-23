package dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import org.bson.conversions.Bson;
import org.example.carrosuenp.Usuario;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.regex;

public class UsuarioDao {
    private final MongoCollection<Usuario> collection;

    public UsuarioDao() {
        MongoDatabase db = MongoConn.db();
        this.collection = db.getCollection("Usuarios", Usuario.class);
        System.out.println("[UsuarioDao] Banco: " + db.getName());
        System.out.println("[UsuarioDao] Coleção: " + collection.getNamespace().getCollectionName());
        System.out.println("[UsuarioDao] Total de documentos: " + collection.countDocuments());
    }


    public void salvar(Usuario usuario) {
        collection.insertOne(usuario);
    }


    public Usuario buscarPorLogin(String login) {
        String quoted = java.util.regex.Pattern.quote(login);
        Bson filtro = regex("login", "^" + quoted + "$", "i"); // case-insensitive
        Usuario u = collection.find(filtro).first();

        System.out.println("[UsuarioDao] buscarPorLogin('" + login + "') -> "
                + (u == null ? "null" : "OK (achou '" + u.getLogin() + "')"));
        return u;
    }


    public List<Usuario> listarTodos() {
        return collection.find()
                .sort(Sorts.ascending("nome"))
                .into(new ArrayList<>());
    }


    public void remover(String login) {
        if (login == null) return;
        collection.deleteOne(eq("login", login));
        System.out.println("[UsuarioDao] Usuário removido: " + login);
    }
}
