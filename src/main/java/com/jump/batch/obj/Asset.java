package com.jump.batch.obj;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@Entity(name = "simple_asset")
@Data @AllArgsConstructor @NoArgsConstructor
public final class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.springframework.data.annotation.Id
    private Integer id;

    @NotBlank
    @Size(max = 255, message = "Asset.label must be less than 255 characters")
    private String label;

}