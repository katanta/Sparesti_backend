package org.ntnu.idi.idatt2106.sparesti.sparestibackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest implements Serializable {
    @NotNull @NotEmpty @NotBlank private String resetID;
    @NotNull @NotEmpty @NotBlank private Long userID;
    @NotNull @NotEmpty @NotBlank private String newPassword;
}
