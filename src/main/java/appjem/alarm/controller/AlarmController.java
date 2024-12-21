package appjem.alarm.controller;

import appjem.alarm.domain.UpdateAlarmRequest;
import appjem.alarm.domain.entity.Alarm;
import appjem.alarm.service.AlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/Alarm")
@Slf4j
public class AlarmController {

    private final AlarmService alarmService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Alarm> save(@RequestPart("alarm") Alarm alarm,
                                      @RequestPart(value = "mp3File", required = false) MultipartFile mp3File) throws IOException {
        log.info("알람 데이터: {}", alarm);
        if (mp3File == null || mp3File.isEmpty()) {
            log.warn("파일이 전송되지 않았습니다.");
        } else {
            log.info("파일 전송됨: {}", mp3File.getOriginalFilename());
        }

        Alarm savedAlarm = alarmService.save(alarm, mp3File);
        return ResponseEntity.ok(savedAlarm);
    }


    @GetMapping
    public List<Alarm> getAll() {
        return alarmService.findAll();
    }

    @GetMapping("/{id}")
    public Alarm getById(@PathVariable Long id) {
        return alarmService.findById(id);
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<String> changeActive(@PathVariable Long id) {
        log.info("Change active for Alarm ID: {}", id);
        alarmService.changeActive(id);
        return ResponseEntity.ok("변경완료");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try {
            alarmService.delete(id);
            return ResponseEntity.ok("삭제완료");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body("알람을 찾을 수 없습니다.");
        }
    }

    @PutMapping("/{id}")
    public Alarm update(@RequestBody UpdateAlarmRequest request) {
        return alarmService.update(request);
    }

    @Scheduled(fixedRate = 60000)
    public void checkAlarms() {
        alarmService.checkAlarms();
    }

    @GetMapping("/check")
    public String checkAlarmsNow() {
        LocalTime currentTime = LocalTime.now();
        List<Alarm> alarms = alarmService.findAll();
        StringBuilder result = new StringBuilder();

        for (Alarm alarm : alarms) {
            if (alarmService.isTimeToTrigger(alarm.getTime(), currentTime)) {
                alarmService.triggerAlarm(alarm);
                result.append("알람 울림: ").append(alarm.getTitle()).append("\n");
            }
        }

        if (result.length() == 0) {
            return "현재 울릴 알람이 없습니다.";
        }
        return result.toString();
    }
}
