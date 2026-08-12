package iaroslav.baranov.tracklog.web;

import iaroslav.baranov.tracklog.processor.CompleteDatabase;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
public class TrackLog {
    @Getter
    @Setter
    private CompleteDatabase db;

}
