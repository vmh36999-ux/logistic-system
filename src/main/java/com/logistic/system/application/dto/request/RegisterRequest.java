package com.logistic.system.application.dto.request;

import java.time.LocalDate;

import com.logistic.system.domain.enums.Gender;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^\\d{10,11}$", message = "Số điện thoại phải có 10-11 chữ số")
    private String phone;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String password;

    @NotBlank(message = "Họ đệm không được để trống")
    private String firstName;

    @NotBlank(message = "Tên không được để trống")
    private String lastName;

    @NotNull(message = "Giới tính không được để trống")
    private Gender gender;

    private LocalDate birthDate;
    @NotNull(message = "Tỉnh/Thành phố là bắt buộc")
    private Long provinceId;

    @NotNull(message = "Quận/Huyện là bắt buộc")
    private Long districtId;

    @NotNull(message = "Phường/Xã là bắt buộc")
    private Long wardId;

    @NotBlank(message = "Địa chỉ chi tiết (số nhà, tên đường) không được để trống")
    private String address;
}
