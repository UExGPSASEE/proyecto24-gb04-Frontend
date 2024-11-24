package model;

import java.time.OffsetDateTime;
import java.util.Objects;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonProperty;




;

/**
 * Comments
 */


public class Comments {

  private Long id;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime uploadDate;

  private Long user;

  private Long video;

  private String text;
  
  private String username;

  public Comments id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * ID único del video (clave primaria).
   * @return id
   */
  
  
  @JsonProperty("id")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Comments uploadDate(OffsetDateTime uploadDate) {
    this.uploadDate = uploadDate;
    return this;
  }

  /**
   * Fecha y hora de subida del comentario.
   * @return uploadDate
   */

  @JsonProperty("uploadDate")
  public OffsetDateTime getUploadDate() {
    return uploadDate;
  }

  public void setUploadDate(OffsetDateTime uploadDate) {
    this.uploadDate = uploadDate;
  }

  public Comments user(Long user) {
    this.user = user;
    return this;
  }

  /**
   * Clave que referencia al usuario que ha realizado el comentario.
   * @return user
   */
  

  @JsonProperty("user")
  public Long getUser() {
    return user;
  }

  public void setUser(Long user) {
    this.user = user;
  }

  public Comments video(Long video) {
    this.video = video;
    return this;
  }

  /**
   * Clave que referencia al video en el que ha realizado el comentario.
   * @return video
   */
  

  @JsonProperty("video")
  public Long getVideo() {
    return video;
  }

  public void setVideo(Long video) {
    this.video = video;
  }

  public Comments text(String text) {
    this.text = text;
    return this;
  }

  /**
   * Texto del comentario.
   * @return text
   */
  

  @JsonProperty("text")
  public String getText() {
    return text;
  }
  


  public void setText(String text) {
    this.text = text;
  }

  @JsonProperty("username")
  public String getUsername() {
    return username;
  }
  
  public void setUsername(String username) {
	    this.username = username;
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Comments comments = (Comments) o;
    return Objects.equals(this.id, comments.id) &&
        Objects.equals(this.uploadDate, comments.uploadDate) &&
        Objects.equals(this.user, comments.user) &&
        Objects.equals(this.video, comments.video) &&
        Objects.equals(this.text, comments.text);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, uploadDate, user, video, text);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Comments {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    uploadDate: ").append(toIndentedString(uploadDate)).append("\n");
    sb.append("    user: ").append(toIndentedString(user)).append("\n");
    sb.append("    video: ").append(toIndentedString(video)).append("\n");
    sb.append("    text: ").append(toIndentedString(text)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

