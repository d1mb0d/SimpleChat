package Client;

import java.util.HashSet;
import java.util.Set;

public class ClientModel {
    private Set<String> users = new HashSet<>();
    protected Set<String> getUsers() {
        return users;
    }
    protected void setUsers(Set<String> users) {
        this.users = users;
    }
    protected void addUsers(String nameUser) {
        users.add(nameUser);
    }
    protected void removeUsers(String nameUser) {
        users.remove(nameUser);
    }
}
