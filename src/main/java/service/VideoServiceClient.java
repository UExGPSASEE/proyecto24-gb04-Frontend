package service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import model.Comments;
import model.Video;
@Service
public class VideoServiceClient {
	private final RestTemplate restTemplate = new RestTemplate();
    private final String videoServiceUrl = "http://localhost:8082"; // URL del microservicio de videos
    
    public List<Video> getUserVideos(Long userId) {
        try {
            String url = videoServiceUrl + "/users/" + userId + "/videos"; // URL del endpoint
            ResponseEntity<Video[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    Video[].class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return Arrays.asList(response.getBody());
            } else {
                return Collections.emptyList(); // Devuelve lista vacía si no hay contenido
            }
        } catch (HttpClientErrorException e) {
            System.err.println("Error al obtener videos del usuario: " + e.getStatusCode());
            return Collections.emptyList(); // Maneja el error y devuelve lista vacía
        }
    }
    
    public Video createVideo(Video video) {
        try {
            return restTemplate.postForObject(videoServiceUrl + "/video", video, Video.class);
        } catch (HttpClientErrorException e) {
            System.err.println("Error al crear el video: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return null; // Manejo de error, podrías lanzar una excepción personalizada
        }
    }
    
    public Video editVideo(Video video) {
        try {
            return restTemplate.postForObject(videoServiceUrl + "/video/edit", video, Video.class);
        } catch (HttpClientErrorException e) {
            System.err.println("Error al editar el video: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return null; // Manejo de error, podrías lanzar una excepción personalizada
        }
    }
    
    public void deleteVideo(Long videoId) {
        try {
            // Construir la URL con parámetros
            String url = videoServiceUrl + "/video/delete/" + videoId;
                                        

            // Realizar la solicitud POST
            restTemplate.postForEntity(url, null, Void.class);

            System.out.println("Video eliminado correctamente");
        } catch (HttpClientErrorException e) {
            System.err.println("Error al eliminar el video: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            throw new RuntimeException("No se pudo eliminar el video. Intenta más tarde.");
        }
    }
    


    public List<Comments> getComments(Long videoId) {
        try {
            String url = videoServiceUrl + "/video/" + videoId + "/comments"; // URL del endpoint
            ResponseEntity<Comments[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    Comments[].class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return Arrays.asList(response.getBody());
            } else {
                return Collections.emptyList(); // Devuelve lista vacía si no hay contenido
            }

            
        } catch (HttpClientErrorException e) {
            System.err.println("Error al obtener comentarios del video: " + e.getStatusCode());
            return Collections.emptyList(); // Maneja el error y devuelve lista vacía
        }
    }
    
    public void createComment(Comments comentario) {
        String endpoint = videoServiceUrl + "/video/comment";
        try {
            // Enviar el comentario al microservicio de videos mediante POST
            restTemplate.postForEntity(endpoint, comentario, Void.class);
            System.out.println("Comentario enviado correctamente al microservicio de videos.");
        } catch (Exception e) {
            // Manejo de errores
            System.err.println("Error al enviar el comentario al microservicio de videos: " + e.getMessage());
            throw e; // Propagar la excepción para que el controlador maneje el error
        }
    }
    
    public void addLike(Long videoId) {
        System.out.println("VideoServiceClient -> Agregar 'Me gusta' al video con ID: " + videoId);

        // Construir la URL del endpoint
        String url = videoServiceUrl + "/video/like";

        try {
            // Crear el cuerpo de la petición con el ID del video
            Map<String, Long> requestBody = new HashMap<>();
            requestBody.put("videoId", videoId);

            // Llamar al microservicio usando RestTemplate
            restTemplate.postForEntity(url, requestBody, Void.class);
            System.out.println("VideoServiceClient -> 'Me gusta' agregado exitosamente al video con ID: " + videoId);
        } catch (RestClientException e) {
            System.err.println("VideoServiceClient -> Error al agregar 'Me gusta': " + e.getMessage());
            throw new RuntimeException("Error al comunicarse con el microservicio de videos para agregar 'Me gusta'", e);
        }
    }
    
    public void addUnlike(Long videoId) {
        System.out.println("VideoServiceClient -> Agregar 'Me gusta' al video con ID: " + videoId);

        // Construir la URL del endpoint
        String url = videoServiceUrl + "/video/unlike";

        try {
            // Crear el cuerpo de la petición con el ID del video
            Map<String, Long> requestBody = new HashMap<>();
            requestBody.put("videoId", videoId);

            // Llamar al microservicio usando RestTemplate
            restTemplate.postForEntity(url, requestBody, Void.class);
            System.out.println("VideoServiceClient -> 'Me gusta' agregado exitosamente al video con ID: " + videoId);
        } catch (RestClientException e) {
            System.err.println("VideoServiceClient -> Error al agregar 'Me gusta': " + e.getMessage());
            throw new RuntimeException("Error al comunicarse con el microservicio de videos para agregar 'Me gusta'", e);
        }
    }
    
    public void addViewVideo(Long videoId) {
        try {
            // Crear el mapa de datos a enviar en el cuerpo de la solicitud
            Map<String, Long> requestBody = new HashMap<>();
            requestBody.put("videoId", videoId);

            // Realizar una solicitud POST al endpoint del microservicio
            ResponseEntity<Void> response = restTemplate.postForEntity(
                videoServiceUrl + "/video/view", // Endpoint del microservicio
                requestBody,                  // Cuerpo de la solicitud
                Void.class                    // Tipo esperado de respuesta (sin cuerpo)
            );

            // Verificar que la respuesta sea OK
            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("Vista registrada correctamente para el video con ID: " + videoId);
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
    
    public Video getVideoById(Long id) {
        try {
            // Construir la URL con el parámetro ID
            String url = videoServiceUrl + "/video/"+ id;

            // Realizar la solicitud GET al endpoint
            return restTemplate.getForObject(url, Video.class);
        } catch (HttpClientErrorException e) {
            System.err.println("Error al obtener el video: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return null; // Manejo del error devolviendo null o lanzando una excepción personalizada
        } catch (Exception e) {
            System.err.println("Error inesperado al obtener el video: " + e.getMessage());
            return null;
        }
    }
    
    
}
