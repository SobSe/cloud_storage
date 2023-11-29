package ru.sobse.cloud_storage.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.BinaryJdbcType;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"id"})
@Getter
@Setter
@Entity
@Table(name = "files")
public class FileDAO implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String fileName;
    @Column(columnDefinition = "bytea")
    @JdbcType(BinaryJdbcType.class)
    private byte[] data;
    private  long size;
    @ManyToOne()
    @JoinColumn(name = "user_id")
    private UserDAO user;

}
