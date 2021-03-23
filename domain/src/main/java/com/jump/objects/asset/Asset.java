package com.jump.objects.asset;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;


@Entity @Table(name = "simple_asset")
@Data @NoArgsConstructor
public final class Asset implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) @org.springframework.data.annotation.Id
    private Long id;


    private String label;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Asset {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    label: ").append(toIndentedString(label)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}