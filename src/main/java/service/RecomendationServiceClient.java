package service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import model.User;
import model.Video;

@Service
public class RecomendationServiceClient {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String recommendationServiceUrl = "http://localhost:8083"; // URL del microservicio de recomendaciones

    /**
     * Obtiene el historial de visualización de videos de un usuario por su nombre de usuario.
     *
     * @param username Nombre de usuario
     * @return Lista de objetos Video vistos por el usuario
     */
    public List<Video> getHistory(String username) {
        try {
            // Construir la URL del endpoint con el nombre de usuario
            String url = recommendationServiceUrl + "/home/" + username + "/history";

            // Realizar la solicitud GET al microservicio
            ResponseEntity<Video[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    Video[].class
            );

            // Si la respuesta es exitosa y contiene datos, retorna la lista
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return Arrays.asList(response.getBody());
            } else {
                // Si no hay datos, retorna una lista vacía
                return Collections.emptyList();
            }
        } catch (HttpClientErrorException e) {
            // Manejo de errores específicos del cliente HTTP
            System.err.println("Error al obtener el historial para el usuario: " + username + " - " + e.getStatusCode());
            return Collections.emptyList();
        } catch (Exception e) {
            // Manejo de errores inesperados
            System.err.println("Error inesperado al obtener el historial para el usuario: " + username + " - " + e.getMessage());
            return Collections.emptyList();
        }
    }
    
    public List<Video> getTopVideos() {
        try {
            // Construir la URL del endpoint con el nombre de usuario
            String url = recommendationServiceUrl + "/home/topVideos";

            // Realizar la solicitud GET al microservicio
            ResponseEntity<Video[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    Video[].class
            );

            // Si la respuesta es exitosa y contiene datos, retorna la lista
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return Arrays.asList(response.getBody());
            } else {
                // Si no hay datos, retorna una lista vacía
                return Collections.emptyList();
            }
        } catch (HttpClientErrorException e) {
            // Manejo de errores específicos del cliente HTTP
            System.err.println("Error al obtener el top de videos " + e.getStatusCode());
            return Collections.emptyList();
        } catch (Exception e) {
            // Manejo de errores inesperados
            System.err.println("Error inesperado al obtener el top de videos para el usuario: "  + e.getMessage());
            return Collections.emptyList();
        }
    }
    
    public List<Video> getVideoGenero(String genero) {
        try {
            // Construir la URL del endpoint con el nombre de usuario
            String url = recommendationServiceUrl + "/home/genres/" + genero;

            // Realizar la solicitud GET al microservicio
            ResponseEntity<Video[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    Video[].class
            );

            // Si la respuesta es exitosa y contiene datos, retorna la lista
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return Arrays.asList(response.getBody());
            } else {
                // Si no hay datos, retorna una lista vacía
                return Collections.emptyList();
            }
        } catch (HttpClientErrorException e) {
            // Manejo de errores específicos del cliente HTTP
            System.err.println("Error al obtener los videos para el genero: " + genero + " - " + e.getStatusCode());
            return Collections.emptyList();
        } catch (Exception e) {
            // Manejo de errores inesperados
            System.err.println("Error inesperado al obtener los videos para el genero: " + genero + " - " + e.getMessage());
            return Collections.emptyList();
        }
    }
    
    public List<Video> getVideoSeguidos(String username) {
        try {
            // Construir la URL del endpoint con el nombre de usuario
            String url = recommendationServiceUrl + "/home/user/" + username + "/following";

            // Realizar la solicitud GET al microservicio
            ResponseEntity<Video[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    Video[].class
            );

            // Si la respuesta es exitosa y contiene datos, retorna la lista
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return Arrays.asList(response.getBody());
            } else {
                // Si no hay datos, retorna una lista vacía
                return Collections.emptyList();
            }
        } catch (HttpClientErrorException e) {
            // Manejo de errores específicos del cliente HTTP
            System.err.println("Error al obtener los videos para el user: " + username + " - " + e.getStatusCode());
            return Collections.emptyList();
        } catch (Exception e) {
            // Manejo de errores inesperados
            System.err.println("Error inesperado al obtener los videos para el user: " + username + " - " + e.getMessage());
            return Collections.emptyList();
        }
    }
    
    public List<Video> getVideoNoSeguidos(String username) {
        try {
            // Construir la URL del endpoint con el nombre de usuario
            String url = recommendationServiceUrl + "/home/user/" + username + "/recommendations";

            // Realizar la solicitud GET al microservicio
            ResponseEntity<Video[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    Video[].class
            );

            // Si la respuesta es exitosa y contiene datos, retorna la lista
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return Arrays.asList(response.getBody());
            } else {
                // Si no hay datos, retorna una lista vacía
                return Collections.emptyList();
            }
        } catch (HttpClientErrorException e) {
            // Manejo de errores específicos del cliente HTTP
            System.err.println("Error al obtener los videos para el user: " + username + " - " + e.getStatusCode());
            return Collections.emptyList();
        } catch (Exception e) {
            // Manejo de errores inesperados
            System.err.println("Error inesperado al obtener los videos para el user: " + username + " - " + e.getMessage());
            return Collections.emptyList();
        }
    }
    
    public List<User> getPerfilesSeguidos(String username) {
        try {
            // Construir la URL del endpoint con el nombre de usuario
            String url = recommendationServiceUrl + "/home/user/" + username + "/followingProfiles";

            // Realizar la solicitud GET al microservicio
            ResponseEntity<User[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    User[].class
            );

            // Si la respuesta es exitosa y contiene datos, retorna la lista
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return Arrays.asList(response.getBody());
            } else {
                // Si no hay datos, retorna una lista vacía
                return Collections.emptyList();
            }
        } catch (HttpClientErrorException e) {
            // Manejo de errores específicos del cliente HTTP
            System.err.println("Error al obtener los seguidos para el user: " + username + " - " + e.getStatusCode());
            return Collections.emptyList();
        } catch (Exception e) {
            // Manejo de errores inesperados
            System.err.println("Error inesperado al obtener los seguidos para el user: " + username + " - " + e.getMessage());
            return Collections.emptyList();
        }
    }
    
    public List<User> getPerfilesNoSeguidos(String username) {
        try {
            // Construir la URL del endpoint con el nombre de usuario
            String url = recommendationServiceUrl + "/home/user/" + username + "/notfollowingProfiles";

            // Realizar la solicitud GET al microservicio
            ResponseEntity<User[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    User[].class
            );

            // Si la respuesta es exitosa y contiene datos, retorna la lista
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return Arrays.asList(response.getBody());
            } else {
                // Si no hay datos, retorna una lista vacía
                return Collections.emptyList();
            }
        } catch (HttpClientErrorException e) {
            // Manejo de errores específicos del cliente HTTP
            System.err.println("Error al obtener los seguidos para el user: " + username + " - " + e.getStatusCode());
            return Collections.emptyList();
        } catch (Exception e) {
            // Manejo de errores inesperados
            System.err.println("Error inesperado al obtener los seguidos para el user: " + username + " - " + e.getMessage());
            return Collections.emptyList();
        }
    }
    
    public List<Video> buscarVideos(String query) {
        try {
            // Construir la URL del endpoint con el nombre de usuario
            String url = recommendationServiceUrl + "/home/search/" + query;

            // Realizar la solicitud GET al microservicio
            ResponseEntity<Video[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    Video[].class
            );

            // Si la respuesta es exitosa y contiene datos, retorna la lista
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return Arrays.asList(response.getBody());
            } else {
                // Si no hay datos, retorna una lista vacía
                return Collections.emptyList();
            }
        } catch (HttpClientErrorException e) {
            // Manejo de errores específicos del cliente HTTP
            System.err.println("Error al obtener los videos para la query: "+ query + "-" + e.getStatusCode());
            return Collections.emptyList();
        } catch (Exception e) {
            // Manejo de errores inesperados
            System.err.println("Error inesperado al obtener los videos para la query: " + query + " - " + e.getMessage());
            return Collections.emptyList();
        }
    }
    
    
}
