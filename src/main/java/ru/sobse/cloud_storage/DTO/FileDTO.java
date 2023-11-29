package ru.sobse.cloud_storage.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class FileDTO {
    @JsonProperty(value = "filename")
    private String fileName;
    private long size;
}
