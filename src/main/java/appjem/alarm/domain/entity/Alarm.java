package appjem.alarm.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@NoArgsConstructor
@Getter
public class Alarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime time;

    private boolean active = true;

    private String title;

    @Lob
    private byte[] mp3Data;

    @Builder
    public Alarm(LocalTime time, String title, byte[] mp3Data) {
        this.time = time;
        this.title = title;
        this.mp3Data = mp3Data;
    }

    public void changeActive() {
        this.active = !this.active;
    }

    public void update(LocalTime time, String title) {
        this.time = time;
        this.title = title;
    }

    public void setMp3Data(byte[] mp3Data) {
        this.mp3Data = mp3Data;
    }
}
