package service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import model.LoginRequest;
import model.User;

@Service
public class UserServiceClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String userServiceUrl = "http://localhost:8081"; // URL del microservicio de usuarios

    public User loginUser(String username, String password) {
        LoginRequest loginRequest = new LoginRequest(username, password);

        try {
            return restTemplate.postForObject(userServiceUrl + "/user/login", loginRequest, User.class);
        } catch (HttpClientErrorException e) {
            // Captura el error 401 si las credenciales no son válidas
            return null; // Puedes lanzar una excepción personalizada si prefieres
        }
    }
    
    public User registerUser(User user) {

        try {
            return restTemplate.postForObject(userServiceUrl + "/user", user, User.class);
        } catch (HttpClientErrorException e) {
            // Captura el error 401 si las credenciales no son válidas
            return null; // Puedes lanzar una excepción personalizada si prefieres
        }
    }
    
    public void deleteUser(String username) {
        try {
            restTemplate.delete(userServiceUrl + "/user/" + username);
            System.out.println("Usuario eliminado correctamente");
        } catch (HttpClientErrorException e) {
            // Maneja los errores, por ejemplo, si el usuario no existe o no se pudo eliminar
            System.err.println("Error al eliminar el usuario: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            // Puedes lanzar una excepción personalizada aquí si prefieres manejar el error en otra capa
        }
    }
    
    public User updateUser(User user) {
        try {
            // Aquí usa RestTemplate para hacer una solicitud PUT y actualizar el usuario
            String url = userServiceUrl + "/user/" + user.getUsername(); // Usa el nombre de usuario como identificador en la URL
            restTemplate.put(url, user); // Realiza la solicitud PUT con el objeto `user`
            System.out.println("Usuario actualizado correctamente.");
            return user; // Devuelve el usuario actualizado
        } catch (HttpClientErrorException e) {
            System.err.println("Error al actualizar el usuario: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return null; // Puedes lanzar una excepción personalizada si prefieres manejar el error en otra capa
        }
    }
    
    public List<User> getAllUsers() {
        try {
            // Realiza una solicitud GET para obtener la lista de usuarios
            ResponseEntity<List<User>> response = restTemplate.exchange(
                userServiceUrl + "/user/list", 
                HttpMethod.GET, 
                null, 
                new ParameterizedTypeReference<List<User>>() {}
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            System.err.println("Error al obtener la lista de usuarios: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return Collections.emptyList(); // Retorna una lista vacía si hay un error
        }
    }
    
    public User getUser(String username) {
        try {
            // Realiza una solicitud GET al endpoint /user/{username} y espera un objeto User como respuesta
            return restTemplate.getForObject(userServiceUrl + "/user/" + username, User.class);
        } catch (HttpClientErrorException e) {
            // Maneja errores HTTP, por ejemplo, si el usuario no existe (404) u otros errores de cliente
            System.err.println("Error al obtener el usuario: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return null; // O lanzar una excepción personalizada si lo prefieres
        }
    }
    public boolean isFollowing(Long userId, String profileUsername) {
        try {
            ResponseEntity<Boolean> response = restTemplate.getForEntity(
                userServiceUrl + "/user/" + userId + "/isFollowing/" + profileUsername,
                Boolean.class
            );
            return response.getBody() != null && response.getBody();
        } catch (HttpClientErrorException e) {
            System.err.println("Error al verificar si el usuario con ID " + userId + " sigue a " + profileUsername);
            return false; // Devuelve false en caso de error
        }
    }
    
    public boolean followUser(Long userId, String username) {
        try {
            // URL para el endpoint de seguimiento en el microservicio
            String url = userServiceUrl + "/user/follow";

            // Crear el mapa de datos a enviar
            Map<String, Object> request = new HashMap<>();
            request.put("userId", userId);
            request.put("usernameToFollow", username);

            // Realizar la solicitud POST
            ResponseEntity<Void> response = restTemplate.postForEntity(url, request, Void.class);

            // Verificar que la solicitud fue exitosa
            return response.getStatusCode() == HttpStatus.OK;
        } catch (HttpClientErrorException e) {
            System.err.println("Error al seguir al usuario: " + e.getStatusCode());
            return false; // Error en la operación
        }
    }
    
    public boolean unfollowUser(Long userId, String username) {
        try {
            // URL para el endpoint de seguimiento en el microservicio
            String url = userServiceUrl + "/user/unfollow";

            // Crear el mapa de datos a enviar
            Map<String, Object> request = new HashMap<>();
            request.put("userId", userId);
            request.put("usernameToFollow", username);

            // Realizar la solicitud POST
            ResponseEntity<Void> response = restTemplate.postForEntity(url, request, Void.class);

            // Verificar que la solicitud fue exitosa
            return response.getStatusCode() == HttpStatus.OK;
        } catch (HttpClientErrorException e) {
            System.err.println("Error al seguir al usuario: " + e.getStatusCode());
            return false; // Error en la operación
        }
    }

    public User getUserById(Long id) {
        try {
            return restTemplate.getForObject(userServiceUrl + "/user/id/" + id, User.class);
        } catch (HttpClientErrorException e) {
            // Maneja errores HTTP, por ejemplo, si el usuario no existe (404) u otros errores de cliente
            System.err.println("Error al obtener el usuario: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return null; // O lanzar una excepción personalizada si lo prefieres
        }
    }
    
    public void addViewVideo(Long videoId, Long userId) {
        try {
            // Crear el mapa de datos a enviar en el cuerpo de la solicitud
            Map<String, Long> requestBody = new HashMap<>();
            requestBody.put("videoId", videoId);
            requestBody.put("userId", userId);

            // Realizar una solicitud POST al endpoint del microservicio
            ResponseEntity<Void> response = restTemplate.postForEntity(
                userServiceUrl + "/user/view", // Endpoint del microservicio
                requestBody,                  // Cuerpo de la solicitud
                Void.class                    // Tipo esperado de respuesta (sin cuerpo)
            );

            // Verificar que la respuesta sea OK
            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("Vista registrada correctamente para el video con ID: " + videoId + " y usuario con ID: " + userId);
            } else {
                System.err.println("No se pudo registrar la vista. Código de estado: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            // Manejar errores específicos de cliente
            System.err.println("Error al registrar la vista: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            // Manejar cualquier otra excepción
            System.err.println("Error inesperado al registrar la vista: " + e.getMessage());
        }
    }
    
    public void addLikeVideo(Long videoId, Long userId) {
        try {
            // Crear el mapa de datos a enviar en el cuerpo de la solicitud
            Map<String, Long> requestBody = new HashMap<>();
            requestBody.put("videoId", videoId);
            requestBody.put("userId", userId);

            // Realizar una solicitud POST al endpoint del microservicio
            ResponseEntity<Void> response = restTemplate.postForEntity(
                userServiceUrl + "/user/like", // Endpoint del microservicio
                requestBody,                  // Cuerpo de la solicitud
                Void.class                    // Tipo esperado de respuesta (sin cuerpo)
            );

            // Verificar que la respuesta sea OK
            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("Vista registrada correctamente para el video con ID: " + videoId + " y usuario con ID: " + userId);
            } else {
                System.err.println("No se pudo registrar la vista. Código de estado: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            // Manejar errores específicos de cliente
            System.err.println("Error al registrar la vista: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            // Manejar cualquier otra excepción
            System.err.println("Error inesperado al registrar la vista: " + e.getMessage());
        }
    }
    
    public void addUnlikeVideo(Long videoId, Long userId) {
        try {
            // Crear el mapa de datos a enviar en el cuerpo de la solicitud
            Map<String, Long> requestBody = new HashMap<>();
            requestBody.put("videoId", videoId);
            requestBody.put("userId", userId);

            // Realizar una solicitud POST al endpoint del microservicio
            ResponseEntity<Void> response = restTemplate.postForEntity(
                userServiceUrl + "/user/unlike", // Endpoint del microservicio
                requestBody,                  // Cuerpo de la solicitud
                Void.class                    // Tipo esperado de respuesta (sin cuerpo)
            );

            // Verificar que la respuesta sea OK
            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("Vista registrada correctamente para el video con ID: " + videoId + " y usuario con ID: " + userId);
            } else {
                System.err.println("No se pudo registrar la vista. Código de estado: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            // Manejar errores específicos de cliente
            System.err.println("Error al registrar la vista: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            // Manejar cualquier otra excepción
            System.err.println("Error inesperado al registrar la vista: " + e.getMessage());
        }
    }

}
