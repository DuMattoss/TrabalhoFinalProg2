package dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.conversions.Bson;
import org.example.carrosuenp.Usuario;

import static com.mongodb.client.model.Filters.regex;

public class UsuarioDao {
    private final MongoCollection<Usuario> collection;

    public UsuarioDao() {
        MongoDatabase db = MongoConn.db();

        // ⚠️ coloque aqui o nome EXATO da coleção como aparece no Compass
        // exemplo: "Usuario" ou "usuarios" — depende de como foi salva
        this.collection = db.getCollection("Usuarios", Usuario.class);

        System.out.println("[UsuarioDao] Banco: " + db.getName());
        System.out.println("[UsuarioDao] Coleção: " + collection.getNamespace().getCollectionName());
        System.out.println("[UsuarioDao] Total de documentos: " + collection.countDocuments());
    }

    public void salvar(Usuario usuario) {
        collection.insertOne(usuario);
    }

    // Busca por login ignorando maiúsculas/minúsculas
    public Usuario buscarPorLogin(String login) {
        String quoted = java.util.regex.Pattern.quote(login);
        Bson filtro = regex("login", "^" + quoted + "$", "i"); // 'i' = case-insensitive
        Usuario u = collection.find(filtro).first();

        System.out.println("[UsuarioDao] buscarPorLogin('" + login + "') -> "
                + (u == null ? "null" : "OK (achou '" + (u != null ? u.getLogin() : "N/A") + "')"));

        return u;
    }
}
