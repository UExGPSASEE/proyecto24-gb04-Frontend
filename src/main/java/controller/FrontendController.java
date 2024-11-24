package controller;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import model.CommentRequest;
import model.Comments;
import model.LoginRequest;
import model.User;
import model.Video;
import service.RecomendationServiceClient;
import service.UserServiceClient;
import service.VideoServiceClient;

@Controller
public class FrontendController {

    @Autowired
    private UserServiceClient userServiceClient;
    
    @Autowired
    private VideoServiceClient videoServiceClient;
    
    @Autowired
    private RecomendationServiceClient recomendationServiceClient;
    
    @GetMapping("/")
    public String home() {
        return "redirect:/home"; // Redirige a la página principal
    }

    /* ----------------------------- Endpoints de usuarios ----------------------------- */ 
    
    @GetMapping("/login")
    public String showLoginPage() {
    	System.out.println("------------ FrontendController -> showLoginPage() ------------");
    	
        return "login"; // Carga la plantilla login.html
    }
    
    @GetMapping("/register")
    public String showRegisterPage() {
    	System.out.println("------------ FrontendController -> showRegisterPage() ------------");
    	
        return "register"; // Carga la plantilla login.html
    }
    
    
    @PostMapping("/user/login")
    @ResponseBody
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest, HttpSession session) {
        System.out.println("------------ FrontendController -> loginUser() ------------");

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        // Llama al servicio de usuario para verificar las credenciales
        User user = userServiceClient.loginUser(username, password);

        if (user != null) {
            System.out.println("Usuario autenticado correctamente");

            // Almacena el usuario en la sesión
            session.setAttribute("user", user);

            // Respuesta 200 OK con un mensaje opcional
            return ResponseEntity.ok().body("Inicio de sesión exitoso");
        } else {
            System.out.println("Inicio de sesión fallido");

            // Respuesta 401 Unauthorized con un mensaje de error
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
        }
    }

    
    @PostMapping("/user/register")
    public ModelAndView registerUser(@RequestBody User user, HttpSession session) {
        System.out.println("------------ FrontendController -> registerUser() ------------");

        // Llama al microservicio de usuarios para registrar al nuevo usuario
        User registeredUser = userServiceClient.registerUser(user);

        if (registeredUser != null) {
        	session.setAttribute("user", registeredUser);
            System.out.println("Usuario registrado exitosamente.");
            
            return new ModelAndView("redirect:/user/profile");
        } else {
            System.out.println("Error al registrar usuario.");
            return new ModelAndView("registerError");
        }
    }
    
    @PostMapping("/user/delete")
    public ModelAndView deleteUser(HttpSession session) {
        System.out.println("------------ FrontendController -> deleteUser() ------------");

        User user = (User) session.getAttribute("user");
        try {
            userServiceClient.deleteUser(user.getUsername());
            System.out.println("Usuario eliminado exitosamente.");

            // Redirige a la página de confirmación de eliminación o al inicio
            return new ModelAndView("redirect:/register");
        } catch (Exception e) {
            System.out.println("Error al eliminar el usuario: " + e.getMessage());
            
            // Redirige a una página de error
            ModelAndView modelAndView = new ModelAndView("deleteError");
            modelAndView.addObject("errorMessage", "No se pudo eliminar el usuario.");
            return modelAndView;
        }
    }
    
    @GetMapping("/user/logout")
    public ModelAndView logoutUser(HttpSession session) {
        System.out.println("------------ FrontendController -> logoutUser() ------------");
        
    	session.removeAttribute("user");
    	
    	return new ModelAndView("redirect:/login");
    }
    
    @GetMapping("/user/profile")
    public ModelAndView viewProfile(HttpSession session) {
        System.out.println("------------ FrontendController -> viewProfile() ------------");

        // Recupera el usuario de la sesión
        User user = (User) session.getAttribute("user");

        // tomamos todos los videos del usuario
        List<Video> userVideos = videoServiceClient.getUserVideos(user.getId());
        
        ModelAndView modelAndView = new ModelAndView("profile");
		
		// Pasar los datos necesarios al modelo
		boolean propio = true;
		boolean siguiendo = false;

		modelAndView.addObject("videos", userVideos);
		modelAndView.addObject("user", user);
		modelAndView.addObject("propio", propio);
		modelAndView.addObject("siguiendo", siguiendo);
		
		return modelAndView;
    }
    
    @GetMapping("/user/editProfile")
    public String showEditProfilePage(HttpSession session, Model model) {
    	System.out.println("------------ FrontendController -> showEditProfilePage() ------------");
        
    	User user = (User) session.getAttribute("user");
        
        model.addAttribute("user", user); // Pasa el objeto usuario al modelo
        return "editProfile"; // Carga el template editProfile.html
    }
    
    @PostMapping("/user/update")
    public ModelAndView updateUser(@RequestBody User user, HttpSession session) {
        System.out.println("------------ FrontendController -> updateUser() ------------");

        // Llama a UserServiceClient para actualizar el usuario en el microservicio de usuarios
        User updatedUser = userServiceClient.updateUser(user);

        if (updatedUser != null) {
            System.out.println("Usuario actualizado correctamente.");
            User userSession = (User) session.getAttribute("user");
            
            updatedUser.setId(userSession.getId());
            updatedUser.setRole(userSession.getRole());
            // Actualiza la sesión del usuario
            session.setAttribute("user", updatedUser);

            // Redirige a la página de perfil, con el usuario actualizado en el modelo
            return new ModelAndView("redirect:/user/profile");
        } else {
            System.out.println("Error al actualizar usuario.");
            return new ModelAndView("updateError"); // Página de error de actualización
        }
    }
    
    @GetMapping("/user/userList")
    public String userList(Model model) {
        System.out.println("------------ FrontendController -> userList() ------------");

        // Llama a UserServiceClient para obtener la lista de usuarios
        List<User> users = userServiceClient.getAllUsers();

        // Añade la lista de usuarios al modelo
        model.addAttribute("users", users);

        return "userList"; // Redirige a la vista "userList" donde se mostrará la lista
    }

    @GetMapping("/user/{username}")
    public String showOtherProfile(@PathVariable String username,Model model, HttpSession session) {
        System.out.println("------------ FrontendController -> showOtherProfile() ------------");

        User user = userServiceClient.getUser(username);
        
        User userSession = (User) session.getAttribute("user");
        boolean propio = false;
        boolean admin = false;
        
        // tomamos todos los videos del usuario
        List<Video> userVideos = videoServiceClient.getUserVideos(user.getId());
       
        System.out.println("ID DEL USUARIO DE LA SESION: " + userSession.getId());
        System.out.println("USUARIO DE LA SESION: " + userSession.getUsername());
     // Verificar si el usuario actual sigue al usuario visualizado usando el ID del usuario actual y el username del perfil
        boolean isFollowing = false;
        if (userSession != null) {
            isFollowing = userServiceClient.isFollowing(userSession.getId(), username);
        }
        
        if (user.getUsername().equals(userSession.getUsername())) {
        	propio = true;
        }
        
        if(userSession.getRole().equals("Admin")) {
        	admin = true;
        }
        
        System.out.println("El usuario de la sesion " + userSession.getUsername() + " sigue al usuario " + username + " ?:" + isFollowing);
        // Añade la lista de usuarios al modelo
        model.addAttribute("admin", admin);
        model.addAttribute("videos", userVideos);
        model.addAttribute("user", user);
        model.addAttribute("propio", propio);
        model.addAttribute("siguiendo", isFollowing);
        return "profile"; // Redirige a la vista "userList" donde se mostrará la lista
    }
    
    @PostMapping("/user/follow")
    public ResponseEntity<Void> followUser(@RequestBody String username, HttpSession session) {
        User actualUser = (User) session.getAttribute("user");

        if (actualUser == null || username == null || username.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Error 401 si no está logueado
        }

        boolean success = userServiceClient.followUser(actualUser.getId(), username.trim());
        return success ? ResponseEntity.ok().build() : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @PostMapping("/user/unfollow")
    public ResponseEntity<Void> unfollowUser(@RequestBody String username, HttpSession session) {
        User actualUser = (User) session.getAttribute("user");

        if (actualUser == null || username == null || username.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Error 401 si no está logueado
        }

        boolean success = userServiceClient.unfollowUser(actualUser.getId(), username.trim());
        return success ? ResponseEntity.ok().build() : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    
    @PostMapping("/user/adminDelete")
    public ModelAndView deleteAdmin(@RequestBody Map<String, String> payload, HttpSession session) {
        System.out.println("------------ FrontendController -> deleteAdmin() ------------");

        User user = (User) session.getAttribute("user");
        String username = payload.get("username"); // Extraer username del JSON
        try {
            if (user.getRole().equals("Admin")) {
                userServiceClient.deleteUser(username);
                System.out.println("Usuario eliminado exitosamente.");
            }
            // Redirige a la página de inicio tras eliminar
            return new ModelAndView("redirect:/home");
        } catch (Exception e) {
            System.out.println("Error al eliminar el usuario: " + e.getMessage());

            // Redirige a una página de error
            ModelAndView modelAndView = new ModelAndView("errorDelete");
            modelAndView.addObject("errorMessage", "No se pudo eliminar el usuario.");
            return modelAndView;
        }
    }
    
    
    @GetMapping("/video/restrictions")
    public String handleRestrictions(
            @RequestParam Long videoId,
            @RequestParam String username,
            Model model) {
        System.out.println("----- VideoRestrictionController -> handleRestrictions() -----");
        System.out.println("Video ID: " + videoId);
        System.out.println("Username: " + username);

        // Buscar el video por ID (simulación)
        Video video = videoServiceClient.getVideoById(videoId);
        if (video == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Video no encontrado");
        }

        // Pasar el video y username al modelo para la vista
        model.addAttribute("video", video);
        model.addAttribute("username", username);

        // Redirigir a una vista específica (por ejemplo, una página de restricciones)
        return "restrictionsAdmin"; // Nombre de la vista Thymeleaf
    }



    @PostMapping("/user/saveRestrictions")
    public ResponseEntity<String> saveRestrictions(@RequestBody Map<String, Object> restricciones) {
        System.out.println("----- RestrictionController -> saveRestrictions() -----");

        try {
            // Obtener el ID del video
            Long videoId = Long.valueOf(restricciones.get("id").toString());
            String username = (String) restricciones.get("username");

            // Obtener la lista de países
            ArrayList<String> countries = (ArrayList<String>) restricciones.get("countries");

            // Obtener el valor de restricción de edad
            boolean ageRestricted = (boolean) restricciones.get("ageRestricted");

            Video video = videoServiceClient.getVideoById(videoId);
            
            User usuario = userServiceClient.getUser(username);
            
            video.setAgeRestricted(ageRestricted);
            video.setCountryRestricted(countries);

            
            video.setUser(usuario.getId());
            video.setDuration("00:30:00");
            video.setLikes(0);
            video.setUploadDate(Timestamp.from(Instant.now())); // Convierte la fecha y hora actuales a Timestamp
            videoServiceClient.editVideo(video);

            // Responder con éxito
            return ResponseEntity.ok("Restricciones guardadas exitosamente.");
        } catch (Exception e) {
            // Manejar errores y devolver una respuesta adecuada
            System.err.println("Error al procesar restricciones: " + e.getMessage());
            return ResponseEntity.status(500).body("Error al guardar las restricciones.");
        }
    }


    /* ----------------------------- Endpoints de videos ----------------------------- */ 
    
    
    @GetMapping("/user/formVideo")
    public String showFormCreateVideo(HttpSession session, Model model) {
        System.out.println("------------ FrontendController -> showFormVideo() ------------");
        boolean edicion = false;
        // Verificar el usuario en la sesión
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new IllegalStateException("El usuario no está en sesión.");
        }
        model.addAttribute("edicion", edicion);
        return "formVideo";
    }
    
    @GetMapping("/user/formEditVideo")
    public String showFormEditVideo(@RequestParam Long id, HttpSession session, Model model) {
        System.out.println("------------ FrontendController -> showFormEditVideo() ------------");
        boolean edicion = true;
        User user = (User) session.getAttribute("user");
        
        
        // Obtener el video a editar usando el ID
        Video videoAEditar = videoServiceClient.getVideoById(id);
        
        if(user==null | user.getId()!=videoAEditar.getUser()) {
        	return "error";
        }
        model.addAttribute("video", videoAEditar);
        model.addAttribute("edicion", edicion);
        return "formVideo";
    }

    
    @PostMapping("/user/uploadVideo")
    public ResponseEntity<Void> uploadVideo(@RequestBody Video video, HttpSession session) {
        System.out.println("------------ FrontendController -> uploadVideo() ------------");
        //System.out.println("Recibido video: " + video);
        User user = (User) session.getAttribute("user");
        video.setUser(user.getId());
        video.setDuration("00:30:00");
        video.setLikes(0);
        video.setUploadDate(Timestamp.from(Instant.now())); // Convierte la fecha y hora actuales a Timestamp
        videoServiceClient.createVideo(video);
        
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/user/editVideo")
    public ResponseEntity<Void> editVideo(@RequestBody Video video, HttpSession session) {
        System.out.println("------------ FrontendController -> uploadVideo() ------------");
        //System.out.println("Recibido video: " + video);
        User user = (User) session.getAttribute("user");
        
        video.setUser(user.getId());
        video.setDuration("00:30:00");
        video.setLikes(0);
        video.setUploadDate(Timestamp.from(Instant.now())); // Convierte la fecha y hora actuales a Timestamp
        videoServiceClient.editVideo(video);
        System.out.println("FrontendController -> video a editar:" + video.getTitle());
        return ResponseEntity.ok().build();
    }
    
    
    @PostMapping("/user/deleteVideo")
    public ResponseEntity<Void> deleteVideo(@RequestBody Long id, HttpSession session) {
    	System.out.println("------------ FrontendController -> deleteVideo() ------------");
    	
    	//System.out.println("++++++++++++++++++++++++++++ id del video a borrar : " + id);

        if (id != null && id >= 0) {
            videoServiceClient.deleteVideo(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }
    
    @GetMapping("/video/preview")
    public String previewVideo(
            @RequestParam Long id, Model model, HttpSession session) {

        System.out.println("FrontendController -> previewVideo()");
        
        Video video = videoServiceClient.getVideoById(id);
        
        //System.out.println("FrontendController -> video obtenido: " + video.getTitle() + " id del autor: " + video.getUser());
        
        User user = userServiceClient.getUserById(video.getUser());
        
        List<Comments> comentarios = videoServiceClient.getComments(video.getId());
        
        model.addAttribute("video", video);
		model.addAttribute("user", user);
		model.addAttribute("comments", comentarios);
		
		return "preview";
    }
    
    @GetMapping("/video/view")
    public String viewVideo(
            @RequestParam Long id, Model model, HttpSession session) {
    	System.out.println("FrontendController -> viewVideo() " );
        //System.out.println("FrontendController -> ver el video: " + title + " fecha: " + uploadDate);
    	 User userSession = (User) session.getAttribute("user");
         
         if(userSession == null) {
         	return "login";
         }

        Video video = videoServiceClient.getVideoById(id);
        
        videoServiceClient.addViewVideo(video.getId());
        
        video.setViews(video.getViews() + 1);
        //System.out.println("FrontendController -> video obtenido: " + video.getTitle() + " id del autor: " + video.getUser());
        
        User user = userServiceClient.getUserById(video.getUser());
        

        // Recuperamos el usuario de la sesión con todos sus datos
        User sesionUsuario = userServiceClient.getUserById(userSession.getId());
        
        userServiceClient.addViewVideo(video.getId(), userSession.getId());
        
        List<Comments> comentarios = videoServiceClient.getComments(video.getId());
        
        List<Long> likedVideos = sesionUsuario.getLikedVideos();
        
        boolean liked = false;
        
        for (Long videolike : likedVideos) {
        	//System.out.println("Video que le ha gustado -------------" + videolike);
        	if(videolike == video.getId()) {
        		liked = true;
        	}
        }
        
        model.addAttribute("liked", liked);
		model.addAttribute("video", video);
		model.addAttribute("user", user);
		model.addAttribute("comments", comentarios);
		
		return "view";
    }
    
    @PostMapping("/user/like")
    public ResponseEntity<Void> darMeGusta(@RequestBody Map<String, Long> requestBody, HttpSession session) {
        Long id = requestBody.get("id"); // Extraer el ID del cuerpo
        System.out.println("FrontendController -> darMeGusta()");
        
        User userSession = (User) session.getAttribute("user");
        Video video = videoServiceClient.getVideoById(id);
        
        try {
            userServiceClient.addLikeVideo(video.getId(), userSession.getId());
            videoServiceClient.addLike(video.getId());

            // Devuelve un estado 201 si se guarda correctamente
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            // Manejo de errores y retorno de estado 500
            System.err.println("Error al dar me gusta: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/user/unlike")
    public ResponseEntity<Void> darNoMeGusta(@RequestBody Map<String, Long> requestBody,HttpSession session) {
    	Long id = requestBody.get("id"); // Extraer el ID del cuerpo
        System.out.println("FrontendController -> darNoMeGusta()");
        
        User userSession = (User) session.getAttribute("user");
        Video video = videoServiceClient.getVideoById(id);
        

        try {
            userServiceClient.addUnlikeVideo(video.getId(), userSession.getId());
            videoServiceClient.addUnlike(video.getId());

            // Devuelve un estado 201 si se guarda correctamente
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            // Manejo de errores y retorno de estado 500
            System.err.println("Error al enviar el comentario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/user/comment")
    public ResponseEntity<Void> enviarComentario(@RequestBody CommentRequest commentRequest, HttpSession session) {
        System.out.println("FrontendController -> Recibiendo comentario para el video ID: " + commentRequest.getVideoId());
        
        Comments comentario = new Comments();
        User userSession = (User) session.getAttribute("user");
        
        //System.out.println("FrontendController -> Usuario que hace el comentario: " + userSession.getUsername());
        comentario.setText(commentRequest.getText());
        comentario.setUploadDate(OffsetDateTime.now());
        comentario.setUser(userSession.getId());
        comentario.setUsername(userSession.getUsername());
        comentario.setVideo(commentRequest.getVideoId());
        
        //System.out.println("FrontendController -> Usuario del comentario: " + comentario.getUsername());
        try {
            // Llama al servicio para guardar el comentario
            videoServiceClient.createComment(comentario);

            // Devuelve un estado 201 si se guarda correctamente
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            // Manejo de errores y retorno de estado 500
            System.err.println("Error al enviar el comentario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /* ----------------------------- Endpoints de recomendaciones ----------------------------- */ 
    
    @GetMapping("/user/history")
    public String viewHistory(HttpSession session, Model model) {
        System.out.println("------------ FrontendController -> viewProfile() ------------");

        // Recupera el usuario de la sesión
        User user = (User) session.getAttribute("user");

        List<Video> userHistory = recomendationServiceClient.getHistory(user.getUsername());
        
        model.addAttribute("history", userHistory);
        
        return "history";
    }
    
    public List<Video> obtenerVideosAleatorios(List<Video> videos, int cantidad) {
        if (videos == null || videos.isEmpty()) {
            return videos;
        }

        // Mezclar aleatoriamente los elementos de la lista
        Collections.shuffle(videos);

        // Devolver una sublista con la cantidad especificada o toda la lista si es menor
        return videos.subList(0, Math.min(cantidad, videos.size()));
    }

    @GetMapping("/home")
    public String principalPage(Model model, HttpSession session) {
        System.out.println("------------ FrontendController ->principalPage() ------------");

        User userSession = (User) session.getAttribute("user");
        boolean logeado = true;
        
        if(userSession == null) {
        	logeado=false;
        }else {
        	
        	List<Video> seguidos = recomendationServiceClient.getVideoSeguidos(userSession.getUsername());
        	
        	// Obtener 5 videos aleatorios
            List<Video> videosSeguidos = obtenerVideosAleatorios(seguidos, 5);
            
            List<Video> noSeguidos = recomendationServiceClient.getVideoNoSeguidos(userSession.getUsername());
            
            model.addAttribute("videosSeguidos", videosSeguidos);
            model.addAttribute("videosnoseguidos", noSeguidos);        
            
        }
        
        // Obtener los videos principales desde el cliente del servicio de recomendación
        List<Video> topVideos = recomendationServiceClient.getTopVideos();
        model.addAttribute("topvideos", topVideos);

        
        // Lista de géneros
        String[] generos = {"comedia", "accion", "drama", "documental", "otros", "terror"};

        // Seleccionar un género aleatorio
        Random random = new Random();
        String generoAleatorio = generos[random.nextInt(generos.length)];
        
        List<Video> videosGenero = recomendationServiceClient.getVideoGenero(generoAleatorio);
        model.addAttribute("videosgenero", videosGenero);
        
        // Agregar el género aleatorio al modelo
        model.addAttribute("genero", generoAleatorio);
        
        
        
        model.addAttribute("logeado", logeado);
        return "home";
    }

    @GetMapping("/user/following/userList")
    public String userFollowingList(Model model,  HttpSession session) {
        System.out.println("------------ FrontendController -> userFollowingList() ------------");

        User userSession = (User) session.getAttribute("user");
        
        // Llama a UserServiceClient para obtener la lista de usuarios
        List<User> users = recomendationServiceClient.getPerfilesSeguidos(userSession.getUsername());

        // Añade la lista de usuarios al modelo
        model.addAttribute("followingUsers", users);
        model.addAttribute("following", true);
        return "userList"; // Redirige a la vista "userList" donde se mostrará la lista
    }
    
    @GetMapping("/user/notfollowing/userList")
    public String userNotFollowingList(Model model,  HttpSession session) {
        System.out.println("------------ FrontendController -> userNotFollowingList() ------------");

        User userSession = (User) session.getAttribute("user");
        
        // Llama a UserServiceClient para obtener la lista de usuarios
        List<User> users = recomendationServiceClient.getPerfilesNoSeguidos(userSession.getUsername());

        // Añade la lista de usuarios al modelo
        model.addAttribute("notFollowingUsers", users);
        model.addAttribute("following", false);
        return "userList"; // Redirige a la vista "userList" donde se mostrará la lista
    }
    
    @GetMapping("/user/search")
    public String searchVideo(@RequestParam("query") String query, Model model) {
        System.out.println("------------ FrontendController -> searchVideo() ------------");
        System.out.println("Query recibido: " + query);

        // Lógica para buscar los videos con el término de búsqueda
        List<Video> resultados = recomendationServiceClient.buscarVideos(query);

        // Guardar los resultados en el modelo
        model.addAttribute("resultados", resultados);

        // Retornar la vista de resultados
        return "result"; // Thymeleaf cargará la vista 'result.html'
    }
    
}