package ru.sobse.cloud_storage.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class FileUpdateDTO {
    @JsonProperty(value = "filename")
    private String fileName;
}
