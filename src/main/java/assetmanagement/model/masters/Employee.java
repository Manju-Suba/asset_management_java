package assetmanagement.model.masters;

import assetmanagement.enumData.ActiveInActive;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Employee {

    @Id
    private String id;
    @NotEmpty
    @NotNull
    private String empId;
    @NotEmpty
    @NotNull
    private String businessId;
    @NotEmpty
    @NotNull
    private String companyId;
    @DBRef
    private Department department;
    @NotNull
    @NotEmpty
    private String fullName;
    @Email
    private String email = null;
    @NotEmpty
    @NotNull
    private String jobRole;
    private String city;
    private String country;
    private LocalDate dateOfJoining;
    private String address;
    @NotEmpty
    @NotNull
    private String costCenter;
    @NotEmpty
    @NotNull
    private String specialRole;
    @NotEmpty
    @NotNull
    private String supervisor;
    private String status = ActiveInActive.ACTIVE.getValue();
    @CreatedBy
    private String createdBy;
    @LastModifiedBy
    private String updatedBy;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public Employee(String id) {
        this.id = id;
    }
}
